package dev.kofe.service.utility;

import dev.kofe.model.Item;
import dev.kofe.model.ItemInOrder;
import dev.kofe.model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

@Component
public class Email {

    @Autowired
    private Environment env;

    private Logger logger = LogManager.getLogger(Email.class);

    public void sendEmailToClient
                                    (Order order,
                                    Factura factura,
                                    List<ItemInOrder> itemInOrderList,
                                    String userEmail) {

        sendEmail(messageToClientBuilder(order, factura, itemInOrderList), userEmail);

    }

    public void sendEmailToManager
                                    (Order order,
                                     Factura factura,
                                     List<ItemInOrder> itemInOrderList,
                                     String managerEmail) {

        sendEmail(messageToManagerBuilder(order, factura, itemInOrderList), managerEmail);

    }

    private void sendEmail (String messageBody, String recipientEmail) {

        Properties props = getPropsSimple();

        try {

            Session session = Session.getInstance(props);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(env.getProperty("email_sender_username")));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Dulce Pedido");
            message.setContent(messageBody, "text/html");
            Transport transport = session.getTransport(env.getProperty("email_sender_protocol"));
            transport.connect(
                    env.getProperty("email_sender_host_server"),
                    Integer.parseInt(env.getProperty("email_sender_port_mime")),
                    env.getProperty("email_sender_username"),
                    env.getProperty("email_sender_password"));
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
            logger.info("SWEET SHOP: email has not sent | " + e.getMessage());
        }

    }

    private String messageToClientBuilder (Order order, Factura factura, List<ItemInOrder> itemInOrderList) {

        String messageBody = "<!DOCTYPE html><html lang=\"es\">" +
                "<link href=\"https://fonts.googleapis.com/css2?family=Mali&display=swap\" rel=\"stylesheet\">" +
                "<style>body {font-family: Mali; color: #9d2e36;} table { border: dotted #fbe5c8;" +
                "padding: 0px; border-spacing: 0px; } .table-noborder { border-style: none;" +
                "border-spacing: 20px;}</style><table><tr>" +
                "<td><a href=\"http://www.dulcesduenas.com\"><img src=\"https://readln.me/dd/top-600x157.png\"></a></td>" +
                "</tr><tr><td>" +
                "<br><br><center><b>&iexcl;Gracias por su pedidi!</b></center><br><br></td></tr><tr><td><br><center><b>Pedido # " +
                factura.getFacturaString(order.getId()) +
                "</center><br><br></td></tr><tr><td><center><table class=\"table-noborder\">";
        /* THE END OF PREFIX */

        /* THE CYCLE PART: */
        StringBuilder cycleMessage = buildCyclePartOfTable(itemInOrderList);

        /* THE PART OF TOTALS: */

        messageBody +=

                new String(cycleMessage) +

                        "</table></center></td></tr>" +
                        "" +
                        "" +
                        "<tr><td align=\"center\">" +
                        "<table class=\"table-noborder\" style=\"border-spacing: 30px;\">\n" +
                        "<tr>" +
                        "<td><br>\n" +
                        "Pedido neto: " + order.getTotal_netto_without_delivery() + " &euro;<br>" +
                        "Entrega: " + order.getDelivery_price_netto() + " &euro;<br>" +
                        "IVA: " + factura.getPercentVATCalculatedFromTotalNettoOfOrderPlusDelivery() + " &euro;<br>" +
                        "<b>Bruto (total): " + factura.getTotalBrutto().stripTrailingZeros() + " &euro;</b><br>" +
                        "</td>" +
                        "</tr>" +
                        "</table>" +
                        "</td></tr>" +
                        /* THE END OF THE PART OF TOTALS */
                        /* THE TAIL PART: */
                        "<tr><td><table class=\"table-noborder\" align=\"right\"><tr><td>" +
                        "<a href=\"http://www.dulcesduenas.com\"><img src=\"https://readln.me/dd/dd-r-150x150.png\"></a>" +
                        "</td><td>&nbsp;</td></tr></table></td></tr>\n" +
                        "<tr align=\"center\" style=\"background-color: #fbe5c8\">\n" +
                        "<td><font size=\"1\" face=\"Sans Serif\" color=\"#9d2e36\">Dulces Due&#241;as, 2022 | test mode</font>" +
                        "</td></tr></table></body></html>";
        /* THE END OF THE HTML MESSAGE */

        return messageBody;
    }

    private String messageToManagerBuilder (Order order, Factura factura, List<ItemInOrder> itemInOrderList) {

        String messageBody = "<!DOCTYPE html><html lang=\"es\">" +
                "<table><tr><td>" +
                "<br><center><b>Pedido # " +
                factura.getFacturaString(order.getId()) +
                "</center><br><br>" +
                "Nombre: "  + order.getName()           + "<br>" +
                "Phone: "   + order.getPhone()          + "<br>" +
                "Email: "   + order.getEmail()          + "<br>" +
                "Dir.: "    + order.getAddress()        + "<br>" +
                "Notes: "   + order.getNotes()          + "<br>" +
                "Tiempo: "  + order.getDateOfCreation() + "<br><br>" +
                "</td></tr><tr><td><center><table>";

        /* THE CYCLE PART: */

        StringBuilder cycleMessage = buildCyclePartOfTable(itemInOrderList);

        /* THE PART OF TOTALS: */

        messageBody +=

                new String(cycleMessage) +

                        "</table></center></td></tr><tr><td align=\"center\">" +
                        "<table class=\"table-noborder\" style=\"border-spacing: 30px;\">\n" +
                        "<tr><td><br>\n" +
                        "Pedido neto: " + order.getTotal_netto_without_delivery() + " &euro;<br>" +
                        "Entrega: " + order.getDelivery_price_netto() + " &euro;<br>" +
                        "IVA: " + factura.getPercentVATCalculatedFromTotalNettoOfOrderPlusDelivery() + " &euro;<br>" +
                        "<b>Bruto (total): " + factura.getTotalBrutto().stripTrailingZeros() + " &euro;</b><br>" +
                        "</td></tr></table></td></tr>" +
                        /* THE END OF THE PART OF TOTALS */
                        "</table></body></html>";
        /* THE END OF THE HTML MESSAGE */

        return messageBody;
    }

    private StringBuilder buildCyclePartOfTable (List<ItemInOrder> itemInOrderList) {

        StringBuilder cycleMessage = new StringBuilder();
        for (ItemInOrder itemInOrder : itemInOrderList) {
            int quantityInOrder = itemInOrder.getOrderedQuantity();
            Item item = itemInOrder.getItem();

            cycleMessage.append("<tr><td><a href=\"" +
                    env.getProperty("SERVER_PREFIX") +
                    "/item" + "/" +
                    item.getId() +
                    "\"><img src=\"" +
                    env.getProperty("SERVER_PREFIX") +
                    "/getitemimg/" +
                    item.getId() +
                    "\" width=\"100\"></a></td><td>&nbsp;</td><td>" +
                    item.getPrice() + " Euro,</td><td>&nbsp;</td><td>" +
                    "Ctd. " + quantityInOrder +  "</td></tr>");
        }

        return cycleMessage;
    }

    private Properties getPropsSimple () {

        Properties props = new Properties();

        props.put("mail.transport.protocol", env.getProperty("email_sender_protocol"));
        props.put("mail.encryption", "tls");
        props.put("mail.smtp.host", env.getProperty("email_sender_host_server"));
        props.put("mail.smtp.port", env.getProperty("email_sender_port_mime"));
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.user", env.getProperty("email_sender_username"));
        props.put("mail.smtp.password", env.getProperty("email_sender_password"));

        return props;
    }

}
