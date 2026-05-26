USE bookstore;

DELIMITER $$

CREATE TRIGGER trg_order_item_after_insert
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
  UPDATE books
  SET stock = CASE WHEN stock - NEW.quantity < 0 THEN 0 ELSE stock - NEW.quantity END
  WHERE id = NEW.book_id;
END$$

DELIMITER ;
