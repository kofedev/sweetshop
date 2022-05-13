package dev.kofe.controller;

/**
 *  ShoppingController
 *
 *  GET:   /
 *  GET:   /page/{number}
 *  GET:   /cart
 *  GET:   /item/{id}
 *  GET:   /deleteFromCart/{id}
 *  GET:   /clearCart
 *  GET:   /order
 *  POST:  /order_processing
 *
 */

import dev.kofe.service.utility.Util;
import dev.kofe.service.utility.Factura;
import dev.kofe.model.*;
import dev.kofe.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ShoppingController {

    @Autowired private Environment env;
    @Autowired private ItemService itemService;
    @Autowired private ItemInCardService itemInCardService;
    @Autowired private CartService cartService;
    @Autowired private Util util;
    @Autowired private OrderService orderService;
    @Autowired private DeliveryService deliveryService;
    @Autowired private ItemInOrderService itemInOrderService;

    private Logger logger = LogManager.getLogger(ShoppingController.class);

    @GetMapping("/")
    public String showMainShoppingPageWithItemsList () {

        return "redirect:/page/1";
    }

    /**
     * Attention. Page number: from 1 !
     */
    @GetMapping("/page/{number}")
    public String getItemsPageWithNumber
                        (@PathVariable ("number") int pageNumber,
                         @CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken,
                         Model model) {

        Cart cart = null;
        List<PageLink> pageLinksList = new LinkedList<>();
        List<Item> itemListForSpecificPage = new ArrayList<>();

        // try to get a cart
        if (cookieUserBrowserToken != null) {
            cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
        }

        // get all items which has not zero "quantity"
        List<Item> itemList = itemService.getAllWhichQuantityIsNotZero();

        if (itemList.size() > 0) {

            for (Item item : itemList) {
                // get max allowed for input parameter "max"
                if (cart != null) {
                    item.setMax(util.getAllowedMaximumForItemConsiderCart(item, cart));
                } else {
                    item.setMax(item.getQuantity());
                }
            }

            // sort item list by title
            itemList =
                    itemList.stream()
                            .sorted(Comparator.comparing(Item::getTitle))
                            .collect(Collectors.toList());

            // get items for the specific page

            // is items for the page are existing?
            int indexFirstElementForThePage = (pageNumber - 1) * Integer.parseInt(env.getProperty("items_per_page"));
            if (indexFirstElementForThePage < itemList.size()) {
                for (int i = indexFirstElementForThePage;
                     (i < itemList.size())
                             && (i < indexFirstElementForThePage + Integer.parseInt(env.getProperty("items_per_page")))
                             && (Integer.parseInt(env.getProperty("items_per_page")) > 0);
                     i++)
                {
                    itemListForSpecificPage.add(itemList.get(i));
                }
            }

            // the array contains links to /controller for the pagination ::
            int quantityOfPages;
            int listCount = (int) itemService.getCountAllWhichQuantityIsNotZero();
            if (Integer.parseInt(env.getProperty("items_per_page")) > 0) {
                quantityOfPages = listCount / Integer.parseInt(env.getProperty("items_per_page"));
                if (listCount % Integer.parseInt(env.getProperty("items_per_page")) != 0) {
                    quantityOfPages++;
                }
            } else {
                quantityOfPages = 0;
            }

            for (int i = 0; i < quantityOfPages; i++) {
                if (i + 1 == pageNumber) {
                    pageLinksList.add(new PageLink("•", "/page/" + (i+1) ));
                } else {
                    pageLinksList.add(new PageLink( i+1,"/page/" + (i+1) ));
                }
            }

            if (pageNumber == quantityOfPages) {
            } else {
                pageLinksList.add(new PageLink("siguiente", "/page/" + (pageNumber + 1)));
            }

            if (pageNumber == 1) {
            } else {
                pageLinksList.add(0, new PageLink("previa", "/page/" + (pageNumber - 1)));
            }

        }

        model.addAttribute("linksForPagesArray", pageLinksList);
        model.addAttribute("itemList", itemListForSpecificPage);

        return "index";
    }

    @GetMapping("/cart")
    public String showShoppingCart(
            @CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken,
            Model model) {

        /**
         * IMPORTANT! Cookie must be existed
         */

        if (cookieUserBrowserToken == null) {
            return "redirect:/";
        }

        Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
        if (cart == null) return "redirect:/";
        List<ItemInCart> itemInCartList = itemInCardService.getAllByCard(cart);
        List<Item> itemList = new ArrayList<>(); // to put in model

        int index = 0;
        for (ItemInCart itemInCart : itemInCartList) {
            Item item = itemInCart.getItem();
            if (item == null) {} //@ToDo

            item.setIndex(index++); // we use this as an index for "iteration" to create the id-elements in the table on the page
            item.setQuantity(itemInCart.getItem().getQuantity());   // IMPORTANT
            item.setChosenQuantity(itemInCart.getChosenQuantity()); // IMPORTANT
            itemList.add(item);
        }

        // business: get total
        cart.setTotal(new BigDecimal("0"));
        for (ItemInCart itemInCart : itemInCartList) {
            cart.setTotal( cart.getTotal().add( itemInCart.getItem().getPrice().multiply(new BigDecimal(itemInCart.getChosenQuantity())) ) );
        }

        // business, calculation
        Factura factura = new Factura(cart.getTotal(), env.getProperty("VAT"));

        // put to model
        model.addAttribute("total", cart.getTotal());
        model.addAttribute("vat", factura.getPercentVATCalculatedFromTotalNettoOfOrder().stripTrailingZeros());
        model.addAttribute("grand_total", factura.getTotalBrutto().stripTrailingZeros());

        // sort item list by title
        itemList =
                itemList.stream()
                        .sorted(Comparator.comparing(Item::getTitle))
                        .collect(Collectors.toList());

        // put list to model
        model.addAttribute("itemList", itemList);

        // put the size of list to model
        model.addAttribute("listsize", itemList.size());

        return "cart";
    }

    /**
     * Show page with info on the item with ID
     */
    @GetMapping("/item/{id}")
    public String getItemInfo(@PathVariable("id") long itemId,
                              @CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken,
                              Model model) {

        // try to get a cart
        Cart cart = null;
        if (cookieUserBrowserToken != null) {
            cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
        }

        Item item = itemService.getById(itemId);
        if (item == null) {} //@ToDo exception

        if (cart != null) {
            item.setMax(util.getAllowedMaximumForItemConsiderCart(item, cart));
        } else {
            item.setMax(item.getQuantity());
        }

        model.addAttribute("item", item);

        return "item";
    }

    @GetMapping("/deleteFromCart/{id}")
    public String deleteItemFromCart
            (@PathVariable("id") long itemId,
             @CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken) {

        Item item = itemService.getById(itemId);

        if (item != null) {
            itemInCardService.deleteAllByItem(item);
        } else {
            // @ToDO
        }

        Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);

        if (cart != null) {
            util.updateCurrentQuantityAllItemsInCart(cart);
        } else {
            // @ToDo
        }

        return "redirect:/cart";
    }

    @GetMapping("/clearCart")
    public String clearCart
            (@CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken) {

        Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
        if (cart != null) {
            itemInCardService.deleteAllByCart(cart);
            Order order = orderService.getOrderByCart(cart);
            if (order != null) {
                itemInOrderService.deleteAllByOrder(order);
                orderService.deleteOrder(order);
            }
        }

        return "redirect:/cart";
    }


    /**
     * Order: the next step after the "cart" stage
     */
    @GetMapping("/order")
    public String makeOrderBasedOnCart(
            @CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken,
            Model model) {

        /**
         * based on cookies only
         */

        Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);

        if (cart != null) {

            Order order = orderService.getOrderByCookie(cookieUserBrowserToken);

            if (order == null) {
                // new order
                order = new Order();
                order.setCookieToken(cookieUserBrowserToken);
                order.setCart(cart);

            } else {
                // clean the order
                itemInOrderService.deleteAllByOrder(order);
            }

            // firstly, save in any case: new or cleaned one
            orderService.saveOrder(order);

            // fill by new items from the cart (current state)
            List<ItemInCart> itemInCartList = itemInCardService.getAllByCard(cart);
            if (itemInCartList != null) {
                for (ItemInCart itemInCart : itemInCartList) {
                    ItemInOrder itemInOrder = new ItemInOrder();
                    itemInOrder.setOrder(order);
                    itemInOrder.setItem(itemInCart.getItem());
                    /**
                     * QUANTITY
                     * allowed quantity will be checked on the stage of the finish order
                     */
                    itemInOrder.setOrderedQuantity(itemInCart.getChosenQuantity());
                    itemInOrderService.saveItemInOrder(itemInOrder);
                }
            }

            // Mar 31: delivery issues, DB, list
            List<Delivery> deliveryList = deliveryService.getAllDeliverOptions();
            model.addAttribute("deliveryList", deliveryList);

            // put the order to the model
            model.addAttribute("order", order);

        } else {
            // cart is null @ToDO - exception
        }

        return "order";
    }

    /**
     * A second stage of the ordering process
     */
    @PostMapping("/order_processing")
    public String orderProcessing (@ModelAttribute("order") Order order,
                                   @CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken) {

        boolean isThereCollision = false; // collision: desire quantity is more than in-stock quantity

        if (order != null) {
            orderService.saveOrder(order);
        } else {
            //@ToDo - exception
        }

        Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
        order.setCart(cart); // because there is trouble with conversion to String
        orderService.saveOrder(order);

        List<ItemInOrder> itemInOrderList = itemInOrderService.getAllItemInOrderByOrder(order);

        if (itemInOrderList == null) {} // @ToDo exception

        for (ItemInOrder itemInOrder : itemInOrderList) {
            int desireQuantityInOrder = itemInOrder.getOrderedQuantity();
            int inStockQuantity = itemInOrder.getItem().getQuantity();
            if (desireQuantityInOrder > inStockQuantity) {
                isThereCollision = true; // it is a collision
                itemInOrder.setOrderedQuantity(inStockQuantity); // correct order
                itemInOrderService.saveItemInOrder(itemInOrder); // save corrected order element
                ItemInCart itemInCart = itemInCardService.getByItem(itemInOrder.getItem());
                itemInCart.setChosenQuantity(inStockQuantity); // correct cart
                itemInCardService.saveItemInCart(itemInCart); // save corrected cart element
            }
        }

        if (isThereCollision) {
            return "redirect:/cart"; //@ToDo ATTENTION! Needs message about a collision
        }

        // ATTENTION: there are two points of calculation: before checkout and after successfully checkout

        BigDecimal total_netto_without_delivery = new BigDecimal("0.0");
        for (ItemInOrder itemInOrder : itemInOrderList) {
            BigDecimal quantityForItem = new BigDecimal(itemInOrder.getOrderedQuantity());
            BigDecimal totalForItem = quantityForItem.multiply(itemInOrder.getItem().getPrice());
            total_netto_without_delivery = total_netto_without_delivery.add(totalForItem);
        }

        // total netto without the delivery
        order.setTotal_netto_without_delivery(total_netto_without_delivery);

        // business: calculation grand total (BRUTTO)
        Factura factura = new Factura(total_netto_without_delivery, order.getDelivery_price_netto(), env.getProperty("VAT"));

        // save current values in the order
        order.setTotal_netto(factura.getTotalNettoOfOrderPlusDelivery()); // netto WITH the delivery
        order.setVat_in_percent(factura.getValueOfVATinPercents());
        order.setVat_value(factura.getPercentVATCalculatedFromTotalNettoOfOrderPlusDelivery());
        order.setTotal_brutto(factura.getTotalBrutto());

        orderService.saveOrder(order);

        return "finish_order";
    }

}
