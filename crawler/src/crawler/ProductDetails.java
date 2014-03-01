package crawler;

/* 
 * The ProductDetails class stores the different parameters of the product.
 * It is currently stores productID, styleID and price of the product.
 * The implementation can be extended to store other parameters like productURL etc accordingly.
 * 
 */
public class ProductDetails {
	int productID;
	int styleID;
	float price;
	
	// 0-argument constructor.
	public ProductDetails()
	{
		productID = 0;
		styleID = 0;
		price = 0;
	}
	// 3-argument constructor.
	public ProductDetails(int prodID, int sID, float mrp)
	{
		productID = prodID;
		styleID = sID;
		price = mrp;
	}
}
