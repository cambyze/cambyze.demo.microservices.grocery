INSERT INTO payment(id, reference, order_Reference, payment_Date, payment_Amount, status, masked_Card_Number)
VALUES (NEXTVAL('PAYMENT_SEQUENCE'), 'PM200956', 'PO2045', sysdate, 120.45, 'VALID', '1234 56** **** 1234');
