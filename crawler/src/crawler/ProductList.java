package crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import crawler.ProductDetails;

/*
 * The ProductList class is to store the valid combinations of product for the query entered.
 * It is an extension of ArrayList which stores FinalProduct (introduced below).
 *  
 */
public class ProductList extends ArrayList<FinalProduct> {

	/*
	 * sort() method to sort the results according to the sum.
	 * Sorted in descending order.
	 * 
	 */
	public void sort(){
		Collections.sort(this, new Comparator<FinalProduct>(){
			public int compare(FinalProduct fp1, FinalProduct fp2)
			{
				return Float.compare(fp2.sum, fp1.sum);
			}
		});
	}
	/*
	 * display() method is to display the results to the user.
	 * This method can be changed according to the way we want the output.
	 * Currently it will write the output to a file "output.txt".
	 * 
	 */
	public void display() throws IOException, UnsupportedEncodingException{
		int size = this.size();
		PrintWriter writer = new PrintWriter("output.txt", "UTF-8"); // output.txt is created and opened for writing.
		writer.println("::Gift Combinations::");
		for(int i=0; i < size; i++) // for all the combinations in the ArrayList
		{
			FinalProduct fp = this.get(i); // Get the FinalProduct
			ProductDetailsLL pdll = fp.pdll; // Get the linkedList
			ProductDetailsList temp = pdll.head;
			while(temp != null) // for all the products in the list
			{
				writer.print("Style(" +temp.styleID+ ") " +temp.price+ " & "); // write in the file or display to user.
				temp = temp.next; // go to the next node in the linkedList
			}
			writer.println(" Sum To: "+fp.sum);
		}
		writer.close(); // Close the file.
	}
}
/*
 * ProductDetailsList extends from ProductDetails.
 * It is basically a list structure adding a reference to itself.
 * 
 */
class ProductDetailsList extends ProductDetails{
	ProductDetailsList next;
	public ProductDetailsList(ProductDetails pd)
	{
		super(pd.productID, pd.styleID, pd.price);
		next = null;
	}
	public ProductDetailsList()
	{
		super();
		next = null;
	}
}
/*
 * ProductDetailsLL class is the linkedList which stores a list of the product in a combination.
 * 
 */
class ProductDetailsLL{
	ProductDetailsList head; // head of the list.
	// 0-argument constructor.
	public ProductDetailsLL(){
		head = null;
	}
	/*
	 * add() method will add ProductDetailsList object to the LinkedList.
	 */
	public void add(ProductDetailsList pdl){
		if(head == null) // If head is null, that means list is empty.
			head = pdl;
		else // head is not null
		{
			ProductDetailsList tmp = head;
			while(tmp.next!=null) // traverse for the last node.
				tmp = tmp.next;
			tmp.next = pdl; // add the reference of the new node to the last node.
		}
	}
}

/*
 * FinalProduct class stores the LinkedList of the products in a combination and the sum.
 * 
 */
class FinalProduct {
	ProductDetailsLL pdll;
	float sum;
	// 2-argument constructor.
	public FinalProduct(ProductDetailsLL list, float amount)
	{
		pdll = list;
		sum = amount;
	}
}