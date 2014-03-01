package crawler;

import java.util.*;

/* 
 * The ProductTable class stores the entire data crawled during pre-processing.
 * The table key is dependent on the range which is set to '10'.
 * The key [string] is basically the range of prices in which the crawled price lies. eg: "40.01-50"
 * The value is an array of arraylist of type ProductDetails.
 * Each index of this array correspond to a value in that range.
 * e.g: index 3 will store all the products with price ranging between [43.01, 44.00] for the key "40.01-50"
 * The ProductDetails object fetched are stored in the arraylist corresponding to the index.
 * It seems to be pretty complicated but it is gooood. :) 
 * 
 */
public class ProductTable extends Hashtable<String, ArrayList<ProductDetails>[]>{
	// range defines the range of prices a key in table hold.
	static int range = 10;
	// elementCount is a developer variable essentially to check if all the products are fetched for not.
	static int elementCount = 0;
	
	// As the data is huge it makes sense to define predefined capacity so that the table doesn't rehase a lot of times.
	public ProductTable(int capacity)
	{
		super(capacity);
	}
	
	/*
	 * add() method will add the ProductDetails object to the arraylist of key and arrayIndex passed.
	 * It takes 3 inputs: key, arrayIndex and ProductDetails object
	 * 
	 */
	void add(String key, int arrayIndex, ProductDetails pd)
	{
		ProductTable.elementCount++;
		
		if(this.containsKey(key)) // Check if the key already exists.
		{
			// Get the array corresponding to the key.
			ArrayList<ProductDetails>[] arr = this.get(key);
			
			if(arr[arrayIndex] != null) // Check if an ArrayList already exists for the index passed for this array.
				arr[arrayIndex].add(pd); // ArrayList exists: simple add the object to it.
			else // ArrayList doesn't exists
			{
				ArrayList<ProductDetails> a = new ArrayList<ProductDetails>(); // Create a new ArrayList
				a.add(pd); // Add the object to it.
				arr[arrayIndex] = a; // store it's reference in the array at the index passed.
			}
		}
		else // The key doesn't exist in the table.
		{
			ArrayList<ProductDetails>[] arr = new ArrayList[range]; // Create a new array of size range.
			if(arr[arrayIndex] != null) // TODO: remove redundant code. Pending because api requests are being throttled 
				arr[arrayIndex].add(pd);
			else // this is where it will always come :P
			{
				ArrayList<ProductDetails> a = new ArrayList<ProductDetails>(); // Create a new ArrayList
				a.add(pd); // Add the object to it.
				arr[arrayIndex] = a; // Store it's reference in the array at the index passed.
			}
			this.put(key, arr); // Add this array and key in the table.
		}
	}
	/*
	 * sort() method will sort all the ArrayList in the table.
	 * Does not take any argument and returns type is void.
	 * 
	 */
	void sort()
	{
		// Getting an enumerated key structure.
		Enumeration<String> enuKey = this.keys();
		while(enuKey.hasMoreElements()) // For all the keys that are present in the table
		{
			String key = enuKey.nextElement(); // Get the key.
			ArrayList<ProductDetails>[] arr = this.get(key); // Get the array for that key.
			for(int i = 0; i < ProductTable.range; i++) // For all the array index.
			{
				if(arr[i] != null) // Check if a arrayList exists for this index
				{
					ArrayList<ProductDetails> arrList = arr[i]; // Get the ArrayList
					// Overload the Collections sort() method for the custom object.
					// The sorting is in descending order.
					Collections.sort(arrList, new Comparator<ProductDetails>(){
						public int compare(ProductDetails pd1, ProductDetails pd2)
						{
							return Float.compare(pd2.price, pd1.price);
						}
					});
				} // End if
			} // End for
		} // End while
		// End of lot of loops :P
	}
}
