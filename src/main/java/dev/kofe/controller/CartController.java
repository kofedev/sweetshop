package dev.kofe.controller;

/**
 *
 *   CartController
 *
 *   GET:  /add/{itemId}/{chosenQuantity}
 *   GET:  /cookie
 *   GET:  /checkcookie
 *   GET:  /cartvalue
 *   GET:  /recalculate
 *   POST: /post_payment
 *
 */

import dev.kofe.service.utility.Util;
import dev.kofe.service.utility.Email;
import dev.kofe.service.utility.Factura;
import dev.kofe.model.*;
import dev.kofe.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
public class CartController {

    @Autowired private Environment env;
    @Autowired private CartService cartService;
    @Autowired private ItemService itemService;
    @Autowired private ItemInCardService itemInCardService;
    @Autowired private ItemInOrderService itemInOrderService;
    @Autowired private OrderService orderService;
    @Autowired private Util util;
    @Autowired private Email email;

    private Logger logger = LogManager.getLogger(CartController.class);

    @GetMapping("/add/{itemId}/{chosenQuantity}")
    @ResponseBody
    public ResponseEntity<?> addItemByIdToCart
            (@PathVariable(value = "itemId") long itemId,
             @PathVariable(value = "chosenQuantity") int chosenQuantity,
             @CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken,
             HttpServletResponse response) {

        int quantityOfItemPutInCartInFact = 0;

        /**
         * Attention:
         * 1) We have to delete cookie after erase the cart (the basket)! * cookie.setMaxAge(0)
         * 2) Cookie must be existed * It needs to call '/cookie' via ajax firstly!
         */

        if (cookieUserBrowserToken == null) {
            logger.info("SWEET SHOP: cookieUserBrowserToken is null. Point 075");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // attempt to find a cart
        Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
        if (cart == null) {
            // create new cart
            cart = new Cart();
            if (cart == null) {
                logger.info("SWEET SHOP: cart has not created. Point 084");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            cart.setCookieToken(cookieUserBrowserToken);
            cartService.saveCart(cart);
        }

        // logic of item
        Item item = itemService.getById(itemId);
        if (item == null) {
            logger.info("SWEET SHOP: item is null. Point 091");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // logic of itemInCart - a "projection" of the item inside the cart (basket)
        ItemInCart itemInCart = itemInCardService.getByItemAndCart(item, cart);
        if (itemInCart == null) {
            itemInCart = new ItemInCart();
            itemInCart.setItem(item);
            itemInCart.setCart(cart);
        }

        int quantityThatAlreadyIs = itemInCart.getChosenQuantity();
        if (chosenQuantity + quantityThatAlreadyIs <= item.getQuantity()) {
            // in summary, user wants to get less than we have, it's OK
            itemInCart.setChosenQuantity(quantityThatAlreadyIs + chosenQuantity);
            quantityOfItemPutInCartInFact = chosenQuantity; // how many items were PUT in this calling in fact
        } else {
            // in summary, user wants to get more than we have, it's a problem
            itemInCart.setChosenQuantity( item.getQuantity() ); // everything that we have
            quantityOfItemPutInCartInFact = item.getQuantity() - quantityThatAlreadyIs;
        }

        // renew total quantity in the cart
        util.updateCurrentQuantityAllItemsInCart(cart);

        itemInCardService.saveItemInCart(itemInCart);
        cartService.saveCart(cart);
        int[] responseArray
              = { quantityOfItemPutInCartInFact,  // [0] how many items we have put (add) to cart
                  item.getQuantity() - itemInCart.getChosenQuantity() }; // [1] how many items user will be able to get also

        return new ResponseEntity<int[]>(responseArray, HttpStatus.OK);
    }

    @GetMapping("/cookie")
    @ResponseBody
    public ResponseEntity<?>
    detectCookieAndGenerateCookieIfNeeds
            (@CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken,
             HttpServletResponse response) {

        boolean isCookieWasDetected;

        if (cookieUserBrowserToken == null) {
            isCookieWasDetected = false;
            String newTokenForCookie = UUID.randomUUID().toString();
            Cookie cookie = new Cookie(env.getProperty("COOKIE_NAME"), newTokenForCookie);
            // cookie configuration
            cookie.setMaxAge(14 * 24 * 60 * 60); // 14 days
            cookie.setSecure(true);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            // pack cookie to a response
            response.addCookie(cookie);

        } else {
            isCookieWasDetected = true;
        }

        return new ResponseEntity<Boolean>(isCookieWasDetected, HttpStatus.OK);
    }

    @GetMapping("/checkcookie")
    @ResponseBody
    public ResponseEntity<?> detectCookie
            (@CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken) {

        if (cookieUserBrowserToken == null) {
            logger.info("SWEET SHOP: Token is null. Point 160");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * CartValue = current quantity of items in a cart
     * return current quantity of items in a cart or zero
     */
    @GetMapping("/cartvalue")
    @ResponseBody
    public ResponseEntity<?> getCurrentCartValue
                (@CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken) {

        int quantityToReturn = 0;
        if (cookieUserBrowserToken != null) {
            Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
            if (cart != null) {
                util.updateCurrentQuantityAllItemsInCart(cart);
                quantityToReturn = cart.getCurrentQuantityAllItems();
            } else {
                // it is ok: cart can be not created at this moment yet
                return new ResponseEntity<Integer>(quantityToReturn, HttpStatus.OK);
            }
        }

        return new ResponseEntity<Integer>(quantityToReturn, HttpStatus.OK);
    }

    /**
     * Recalculate the cart
     */
    @PostMapping("/recalculate")
    public ResponseEntity<?>  reCalculateTheCart (
            @CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken,
            @RequestBody String currentValues) throws Exception {

        String newString = "";

        for (int i = 0; i < currentValues.length(); i++) {
            if (currentValues.charAt(i) != '\"') {
                newString += currentValues.charAt(i);
            }
        }

        JSONArray jsonArray = new JSONArray(newString);
        Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
        if (cart != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                Long id = jsonObject.getLong("id"); // get ID of the item
                Item item = itemService.getById(id);
                if (item != null) {
                    ItemInCart itemInCart = itemInCardService.getByItemAndCart(item, cart);
                    if (itemInCart != null) {
                        itemInCart.setChosenQuantity(jsonObject.getInt("value"));
                        itemInCardService.saveItemInCart(itemInCart);
                    } else {
                        throw new Exception("SWEET SHOP: itemInCart is null. Point 224");
                    }
                    util.updateCurrentQuantityAllItemsInCart(cart);
                } else {
                    throw new Exception("SWEET SHOP: item is null. Point 227");
                }
            }
        } else {
            throw new Exception("SWEET SHOP: cart is null. Point 231");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Post-payment processing (a final step of the ordering)
     * Corrects the database.
     */
    @PostMapping("/post_payment")
    @ResponseBody
    public ResponseEntity<?> postPaymentCorrectDataBase
                    (@CookieValue(name = "${COOKIE_NAME}", required = false) String cookieUserBrowserToken,
                     HttpServletRequest request, HttpServletResponse response) {

        String referer = request.getHeader("referer");
        final String REFERRER_PATTERN = env.getProperty("SERVER_PREFIX") + "/charge";

        if (referer.equals(REFERRER_PATTERN)) {
                Cart cart = cartService.getCartByCookieToken(cookieUserBrowserToken);
                Order order = orderService.getOrderByCart(cart);
                String userEmail = order.getEmail();
                List<ItemInOrder> itemInOrderList = itemInOrderService.getAllItemInOrderByOrder(order);
                BigDecimal total_netto_without_delivery = new BigDecimal("0");

                /**
                 * Cycle: Correct the items (in-stock)
                 */
                for (ItemInOrder itemInOrder : itemInOrderList) {

                    int quantityInOrder = itemInOrder.getOrderedQuantity();
                    Item item = itemInOrder.getItem();
                    int inStockQuantity = item.getQuantity();

                    // ATTENTION! IF "UPDATE_STOCK" IS FALSE - STOCK WILL BE UPDATED
                    if (env.getProperty("UPDATE_STOCK").equals("true")) {
                        int newInStockQuantity = Math.max((inStockQuantity - quantityInOrder), 0);
                        item.setQuantity(newInStockQuantity);
                        itemService.saveItem(item);
                    }
                    BigDecimal total_price_of_item_netto =
                            item.getPrice().multiply(BigDecimal.valueOf(quantityInOrder));
                    total_netto_without_delivery = total_netto_without_delivery.add(total_price_of_item_netto);
                }

                // save total netto WITHOUT delivery
                order.setTotal_netto_without_delivery(total_netto_without_delivery);

                // business: class for calculation
                Factura factura = new Factura(
                        total_netto_without_delivery,
                        order.getDelivery_price_netto(),
                        env.getProperty("VAT"));
                order.setTotal_netto(factura.getTotalNettoOfOrderPlusDelivery()); // save total netto with the delivery

                // faktura-invoice
                order.setFactura(factura.getFacturaString(order.getId()));

                // final stage: change order's status and save invoice data
                order.setTotal_brutto(factura.getTotalBrutto().stripTrailingZeros());
                order.setVat_value(factura.getPercentVATCalculatedFromTotalNettoOfOrderPlusDelivery());
                order.setVat_in_percent(factura.getValueOfVATinPercents());
                order.setStatus(OrderStatus.PAID);
                orderService.saveOrder(order);

                // delete "id" cookie
                if (cookieUserBrowserToken != null) {
                      Cookie cookie = new Cookie(env.getProperty("COOKIE_NAME"), "");
                      // cookie configuration
                      cookie.setMaxAge(0); // to delete the cookies
                      cookie.setSecure(true);
                      cookie.setHttpOnly(true);
                      cookie.setPath("/");
                      // pack cookie to a response
                      response.addCookie(cookie);
                }

                // SENDS EMAIL to client and to manager:
                if (userEmail != null && !userEmail.isEmpty()) {

                    email.sendEmailToClient(order, factura, itemInOrderList, userEmail);
                    email.sendEmailToManager(order, factura, itemInOrderList, env.getProperty("manager_email"));

                }

                // Clean the cart
                itemInCardService.deleteAllByCart(cart);
                // preparation to delete
                cart.setUser(null);
                order.setCart(null);
                // delete
                cartService.deleteCart(cart);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}


