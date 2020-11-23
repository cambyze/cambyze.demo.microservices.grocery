INSERT INTO purchase_order(id, reference, product_Reference, order_Date, quantity, amount, balance, paid)
VALUES (NEXTVAL('PURCHASE_ORDER_SEQUENCE'), 'PO2045', 'SDW16' , sysdate, 3, 10500, 0, true);
INSERT INTO purchase_order(id, reference, product_Reference, order_Date, quantity, amount, balance, paid)
VALUES (NEXTVAL('PURCHASE_ORDER_SEQUENCE'),'PO2048', 'SDW16' , sysdate, 15, 300, 300, false);
INSERT INTO purchase_order(id, reference, product_Reference, order_Date, quantity, amount, balance, paid)
VALUES (NEXTVAL('PURCHASE_ORDER_SEQUENCE'),'PO2049', 'SDW14' , sysdate, 20, 400, 400, false);