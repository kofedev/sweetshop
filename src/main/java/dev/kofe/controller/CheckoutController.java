package dev.kofe.controller;

/**
 * Part of Stripe API
 */

import dev.kofe.service.stripe.ChargeRequest;
import dev.kofe.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckoutController {

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    @PostMapping("/checkout")
    public String checkout(
            @ModelAttribute("order") Order order,
            Model model) {

        String draftAmount = String.valueOf(order.getTotal_brutto().doubleValue() * 100); // in cents
        String stringToConvert = "";
        for (int i = 0; i < draftAmount.length(); i++) {
            if (draftAmount.charAt(i) == '.') break;
            stringToConvert += draftAmount.charAt(i);
        }

        int amount = Integer.parseInt(stringToConvert);

        model.addAttribute("pure_amount", order.getTotal_brutto().stripTrailingZeros());

        model.addAttribute("amount", amount); // in cents
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.EUR);

        return "checkout";
    }
}