package dev.kofe.service.utility.init;

import dev.kofe.model.Delivery;
import dev.kofe.model.Role;
import dev.kofe.model.User;
import dev.kofe.repo.DeliveryRepository;
import dev.kofe.repo.RoleRepository;
import dev.kofe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class DataBaseInit {

    @Autowired private RoleRepository roleRepository;
    @Autowired private UserService userService;
    @Autowired private DeliveryRepository deliveryRepository;
    @Autowired private Environment env;

    private Logger logger = LogManager.getLogger(DataBaseInit.class);

    //@ToDo: Flyway

    @PostConstruct
    private void postConstruct() throws Exception {

        /*
         * Issues: Role, Admin, Zero delivery option
         */

        if (roleRepository.count() == 0) {

            // tables is empty, first start

            Role role_admin = new Role();
            role_admin.setName("ROLE_ADMIN");

            Role role_user = new Role();
            role_user.setName("ROLE_USER");

            roleRepository.save(role_admin);
            roleRepository.save(role_user);

            logger.info("SWEET SHOP @PostConstruct: Role's Database is initialized");

            User user = new User();

            user.setUsername(env.getProperty("admin_user_name"));
            user.setPassword(env.getProperty("admin_password"));
            user.setConfirmed(true);

            if (userService.saveAdminUser(user)) {
                logger.info("SWEET SHOP @PostConstruct: Admin user is initialized");
            } else {
                throw new Exception("SWEET SHOP @PostConstruct: Admin is not initialized");
            }

        } else {
            logger.info("SWEET SHOP @PostConstruct: Admin user and Role are present");
        }

        if (deliveryRepository.count() == 0) {

            Delivery delivery = new Delivery();
            delivery.setDescription(env.getProperty("zero_delivery_description"));
            delivery.setPrice(new BigDecimal(env.getProperty("zero_delivery_netto_price")));

            if (deliveryRepository.save(delivery) != null) {
                logger.info("SWEET SHOP @PostConstruct: Zero delivery option is initialized");
            } else {
                throw new Exception("SWEET SHOP @PostConstruct: Zero delivery option is not initialized");
            }

        } else {
            logger.info("SWEET SHOP @PostConstruct: Zero delivery option is present");
        }

    }

}
