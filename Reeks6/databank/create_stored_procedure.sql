\c iii;
/*DROP FUNCTION IF EXISTS get_total;*/
CREATE FUNCTION get_total(IN customer int) RETURNS FLOAT AS $$
DECLARE
  opdracht text;
  total NUMERIC;
BEGIN
	opdracht = 'select sum(quantityordered*priceeach) from orders o join orderdetails od 
	on od.ordernumber = o.ordernumber where customernumber =' || customer;
	EXECUTE opdracht INTO total;
	RETURN total;
END;
$$ LANGUAGE plpgsql;
