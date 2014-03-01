package crawler;
import java.io.*;
import java.net.URL;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

import crawler.ProductTable;
import crawler.ProductList;
import crawler.ProductDetails;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * test class does a lot of work but it didn't get a great name :P
 * It handles the crawling, asks for input from the user, most importantly computes the combination of prices.
 * 
 */
public class test {
	
	static int range = ProductTable.range; // the range of prices stored corresponding to one key in the table.
	// Static stack for the recursion to store possible combinations.
	static Stack<Integer> my_stack = new Stack<Integer>();
	// The table which will store all the crawled data.
	// The capacity is hardcoded. But we can write prediction logic to dynamically predict the capacity of the table.
	static ProductTable table = new ProductTable(500);
	// final_list which will store all the combinations for the query.
	static ProductList final_list = new ProductList();
	// The limit is the maximum number of results returned to the user.
	// This value can be decreased or increased. Practically it makes sense because user would not look for so many results. 
	static int limit = 5000;
	// The value inputed by the user.
	static int value;

	/*
	 * main() method
	 */
	public static void main(String[] args) throws IOException, ParseException{
		// TODO Auto-generated method stub
		
		long time = System.currentTimeMillis();
		System.out.println("Crawl Started: It will take approx 6-8 mins");
		crawl(); // this method will crawl the whole data and will store it in the table.
		time = System.currentTimeMillis() - time;
		System.out.println("Crawl Completed: " +time+ " milliseconds"); // printing time for timing analysis of crawling.
		
		time = System.currentTimeMillis();
		table.sort(); // will sort all the ArrayList stored in the table
		time = System.currentTimeMillis() - time;
		System.out.println("Sorting Completed: " +time+ " milliseconds"); // printing time for timing analysis of sort operation.
		//int count = 0;
		Scanner input = new Scanner(System.in); // Taking input from the user.
		String str_input = "";
		System.out.println("Input the amount and no. of gifts or quit to exit");
		System.out.print("Format (amount,no. of gifts): ");
		while(!(str_input = input.nextLine()).equals("quit")) // takes input unless it is 'quit'
		{
			String[] split = str_input.split(",");
			value = Integer.parseInt(split[0]); // the user desired amount.
			int n = Integer.parseInt(split[1]); // the number of gifts user is looking for.
			time = System.currentTimeMillis();
			rec(value, value, n); // recursive call to compute the possible combination for the inputed query.
			final_list.sort(); // Sorting the results: Redundant if the sum is checked for value in addToFinalList().
			final_list.display(); // display the results to the user.
			time = System.currentTimeMillis() - time;
			System.out.println("\n::::Please check 'output.txt' for top combinations:::: " +time+ " milliseconds"); // printing time for analysis.
			System.out.println("Input the amount and no. of gifts or quit to exit");
			System.out.print("Format (amount,no. of gifts): ");
			final_list.clear(); // clear the list before asking for new query.
		}
	}
	
	/*
	 * rec() is a recursive method which gives all the possible combinations for the query input.
	 * 3 input arguments:
	 * preValue [int] is the value of the previous gift chosen.
	 * value [int] is the budget amount still left.
	 * n [int] is the number of gifts to select.
	 * According to me this is most important method of this whole program. Enjoy!! :)
	 *  
	 */
	private static void rec(int preValue, int value, int n)
	{
		/*
		 * terminating conditions:
		 * value == 0 && n>0, means you are out of budget but still wants to buy more gifts.
		 * value < 0, means the budget is negative. Well that is not allowed.
		 * final_list.size() >= limit, means we have gathered enough results. This condition can be removed to get all results.
		 */
		if(value == 0 && n>0 || value < 0 || final_list.size() >= limit)
			return;
		/*
		 * Whooaa!! This is the only success terminating condition.
		 * All the budget is used and exact number of gifts are selected. 
		 */
		if(n==0)
		{
			checkStack(); // the stack will contain the prices for the success condition.
			return;
		}
		/*
		 * the recursion works on the range of prices that this particular gift can have.
		 * range will be [value/n , preValue]
		 * This allows the program to converge quickly and avoids duplicates.
		 */
		int i = ceil(((float)value/(float)n)); // well, you what ceiling value is...
		while(i <= preValue) // checking for the range.
		{
			if(checkTableForThisPrice(i)) // check the table if this price exists in the table or not.
			{
				my_stack.push(i); // Push the price value in stack.
				rec(i, value-i, n-1); // check for the next gift which would go with this one.
				my_stack.pop(); // Pop the price value from stack.
			}
			i++; // increase the value
		}
	}
	
	/*
	 * ceil() method returns the ceiling integer value
	 */
	private static int ceil(float value)
	{
		int result = (int)value;
		if(result < value)
			result++;
		return result;
	}
	
	/*
	 * checkStack() method will pop all the elements from the stack and store them in an array then puch them back again.
	 * crazy right!! maybe we can improve here.
	 */
	private static void checkStack()
	{
		int size = my_stack.size();
		int[] tmpArr = new int[size];
		int i = 0;
		while(my_stack.size() != 0) // for all the values in stack store them in an array.
			tmpArr[i++] = my_stack.pop();
		while(i > 0)
			my_stack.push(tmpArr[--i]); // push them back again.
		addToFinalList(tmpArr); // pass this array to addToFinalList() which will populate the final_list.
	}
	
	/*
	 * addToFinalList() method will fetch products from the table and add them to the final_list.
	 * TODO: Currently it will add only one combination for each price combination found.
	 * 	   : We can make much more combinations. I ran out of time :( 
	 */
	private static void addToFinalList(int[] arr)
	{ 
		int size = arr.length;
		float sum = 0;
		ProductDetailsLL pdll = new ProductDetailsLL(); // create a new ProductDetailsLL for the combination.
		
		while(size > 0)
		{
			int price = arr[size-1];
			String pRange = getPriceRange(price);
			int arrIndex = getArrIndex(price);
			ArrayList<ProductDetails>[] array = table.get(pRange);
			ArrayList<ProductDetails>al = array[arrIndex];
			ProductDetails pd = al.get(0);
			ProductDetailsList pdl = new ProductDetailsList(pd);
			sum += pd.price;
			size--;
			pdll.add(pdl);
		}
		if(sum == value)
		{
			FinalProduct fp = new FinalProduct(pdll ,sum);
			final_list.add(fp);
		}
	}
	
	/*
	 * checkTableForThisPrice() method takes price as an input and will check if that price is present in the table.
	 * return type: true, if present and else false.
	 * 
	 */
	private static boolean checkTableForThisPrice(int checkPrice)
	{
		String key = getPriceRange((float)checkPrice); // get the table key for this price
		if(table.containsKey(key)) // check if the key exists
		{
			ArrayList<ProductDetails>[] arr = table.get(key); // get the array
			int arrIndex = getArrIndex(checkPrice); // get the arrayIndex for the price
			if(arr[arrIndex] != null)
				return true; // price is present and return true.
			else
				return false; // price not present in table and return false.
		}
		return false; // the key doesn't exists
	}
	
	/*
	 * getPriceRange() method will generate the key of the table for the input price.
	 */
	private static String getPriceRange(float price)
	{
		int range_tmp = ((int)price/range)*range; // eg: for price = 46, range_tmp = 40
		String pRange = "";
		if(range_tmp != price)
			pRange = ((String.valueOf(range_tmp)).concat(".01-")).concat(String.valueOf(range_tmp + range));
		else
			pRange = ((String.valueOf(range_tmp - range)).concat(".01-")).concat(String.valueOf(range_tmp));
		return pRange;
	}
	
	/*
	 * getArrIndex() method will return the index of the array for the input price 
	 */
	private static int getArrIndex(float price)
	{
		int range_tmp = ((int)price/range)*range; // eg: for price = 46.05, range_tmp = 40
		int arrIndex = (int)price - range_tmp; // eg: arrIndex = 6
		float indexCheck = price - (float)range_tmp; // indexCheck = 6.05
		if(price == range_tmp) // eg: price = 50 should be stored in the last index.
			arrIndex = range-1;
		if(arrIndex == indexCheck) // eg: price = 46 should be stored in index = 5.
			arrIndex--;
		return arrIndex; // else return the index computed. eg: for price 46.05, index = 6
		// the example given are according to range 10.
	}
	
	/*
	 * crawl() method as the name suggest the crawls the data from the website using search api.
	 * 
	 */
	private static void crawl() throws ParseException, IOException
	{
		// set of keys which can be used for crawling.
		String[] key = {"b05dcd698e5ca2eab4a0cd1eee4117e7db2a10c4",
				        "12c3302e49b9b40ab8a222d7cf79a69ad11ffd78",
				        "5b8384087156eb88dce1a1d321c945564f4d858e",
				        "a73121520492f88dc3d33daf2103d7574f1a3166"};
		
		int currentResultCount = 0; // to store the results fetched. Used as terminating condition.
		int pageNumber = 1; // page number that is being fetched.
		int k = 1; // which key to be used.
		do{
			//String url = "file:///C:/Users/Ishh/workspace/sample%20search.html"; // sample input
			String apiKey = key[k]; // apikey
			String url = "http://api.zappos.com/Search?term=%20&limit=100&page=" + pageNumber+ "&key=" +apiKey; // the url
			URL my_url = new URL(url);
			InputStream input2;
			try {
				input2 = my_url.openStream(); // open the url
			} catch (IOException e) {
				// if it fails the program should keep on trying with different apikey.
				if(k == 3)
					k = 0;
				else
					k++;
				continue;
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(input2));
			String jsonText = readAll(input); // get the fetched data in form of a string.
			JSONParser parser = new JSONParser(); // parser object
			JSONObject json = (JSONObject) parser.parse(jsonText); // parsed to a string.
			
			String status = (String)json.get("statusCode");
			// Check for the statusCode.
			// TODO: checks for other statusCode missing.
			if(status.compareTo("200") == 0)
			{
				JSONArray resultsArray = (JSONArray)json.get("results"); // get the result array.
				int arrSize = Integer.parseInt((String)json.get("currentResultCount")); // get the result count.
				currentResultCount = arrSize;
				while(arrSize > 0) // for all the results fetched
				{
					arrSize--;
					JSONObject tempJson = (JSONObject)resultsArray.get(arrSize); // get the json object.
					int productId = Integer.parseInt((String)tempJson.get("productId")); // get the productID.
					int styleId = Integer.parseInt((String) tempJson.get("styleId")); // get the styleID.
					String sPrice = ((String) tempJson.get("price")).replace('$', '0'); // get the price.
					if(sPrice.contains(",")) // remove ',' if it contains.
					{
						String[] splitPrice = sPrice.split(",");
						int i = 0;
						sPrice = "";
						while(i < splitPrice.length)
							sPrice = sPrice + splitPrice[i++];
					}
					float price = Float.parseFloat(sPrice); // convert it to float value
					ProductDetails pd = new ProductDetails(productId,styleId, price); // make a new ProductDetails object

					String pRange = getPriceRange(price); // get the corresponding key.
					int arrIndex = getArrIndex(price); // get the corresponding array index.
					table.add(pRange, arrIndex, pd); // add this product to the table.
				}
				pageNumber++; // increment page number to fetch the other page.
			}
			//System.out.println(pageNumber+ " ,");
		//}while(pageNumber < 3);
		}while(currentResultCount != 0); // terminating condition: when returned results are zero.
	}
	
	/*
	 * readAll() method reads the reader object and returns a string
	 */
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }

}
