package edu.eci.arsw.myrestaurant.restcontrollers;

import edu.eci.arsw.myrestaurant.beans.impl.BasicBillCalculator;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/orders")
public class OrdersAPIController {
    @Autowired
    RestaurantOrderServicesStub servicesStub;
    @Autowired
    BasicBillCalculator basicBillCalculator;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders() throws OrderServicesException {
        servicesStub.setBillCalculator(basicBillCalculator);
        Set<Integer> tablesWithOrders = servicesStub.getTablesWithOrders();
        List<Map<String, Object>> ordersList = new ArrayList<>();

        for (Integer tableNumber : tablesWithOrders) {
            Order order = servicesStub.getTableOrder(tableNumber);

            if (order != null) {
                Map<String, Object> orderInfo = new ConcurrentHashMap<>();
                orderInfo.put("tableNumber", order.getTableNumber());
                orderInfo.put("products", order.getOrderedDishes());
                orderInfo.put("total", servicesStub.calculateTableBill(order.getTableNumber()));

                ordersList.add(orderInfo);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("orders", ordersList);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
