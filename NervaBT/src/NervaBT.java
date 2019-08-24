import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.*;

public class NervaBT {

	private static int trades = 0;
	private static BigDecimal totalPips;
	private static double priceStore = 0;
	private static String lastOrder = "";
	private static int orderNo = 0;
	private static ArrayList<String> dateString;
	private static ArrayList<String> timeString = new ArrayList<>();
	private static ArrayList<String> orderType = new ArrayList<>();
	private static int orderNumber = 0;
	private static List<Integer[]> columns;
	private static int count = 0;
	private static int candleGBPUSD = 0;

	static private BigDecimal pips = new BigDecimal(0);

	private static ArrayList<String> dateGBPUSD = new ArrayList<String>();
	private static ArrayList<String> timeGBPUSD = new ArrayList<String>();
	private static ArrayList<Double> priceGBPUSD = new ArrayList<Double>();

	private static NumberFormat formatter = new DecimalFormat("#0.00000");

	public static void main(String[] args) {

		totalPips = new BigDecimal(0);

		List<Integer[]> columns = new ArrayList<>();
		dateString = new ArrayList<String>();

		File file = new File("C:\\Users\\justi\\eclipse-workspace\\NervaBT\\src\\nerva.txt");
		File file2 = new File("C:\\Users\\justi\\eclipse-workspace\\NervaBT\\src\\GBPUSD1.txt");
		BufferedReader bufferedReader = null;
		BufferedReader bufferedReader2 = null;

		try {

			// Scan backtest on EURGBP*************************

			FileReader fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);
			String line;
			line = bufferedReader.readLine();

			while ((line = bufferedReader.readLine()) != null) {
				// System.out.println(line);

				String[] tokens = line.split("\t");
				dateString.add(tokens[1].substring(0, 10));
				timeString.add(tokens[1].substring(11, 16));
				orderType.add(tokens[2]);
				// System.out.println(orderType.get(count));
				count++;

			}

			// Scan GBPUSD*******************************

			FileReader fileReader2 = new FileReader(file2);
			bufferedReader2 = new BufferedReader(fileReader2);
			String line2;

			line2 = bufferedReader2.readLine();

			while ((line2 = bufferedReader2.readLine()) != null) {
				String[] tokens2 = line2.split("\t");

				dateGBPUSD.add(tokens2[0]);
				timeGBPUSD.add(tokens2[1]);
				priceGBPUSD.add(Double.parseDouble(tokens2[2]));

				candleGBPUSD++;

			}

			for (int i = 0; i < timeGBPUSD.size(); i++) {
				if (timeGBPUSD.get(i).length() == 4) {

					String k = "0" + timeGBPUSD.get(i);
					timeGBPUSD.set(i, k);
//					System.out.println(timeGBPUSD.get(i));
				}

			}

			for (int i = 0; i < timeGBPUSD.size(); i++) {

//				System.out.println(dateGBPUSD.get(i) + " " + timeGBPUSD.get(i) + " "+ priceGBPUSD.get(i));

				for (int j = 0; j < timeString.size(); j++) {

					// System.out.println(dateString.get(j) + " " + timeString.get(j) + " " +
					// orderType.get(j));

					if (dateGBPUSD.get(i).equals(dateString.get(j)) && timeGBPUSD.get(i).equals(timeString.get(j))) {
						System.out.println(dateString.get(j) + " " + timeString.get(j) + " " + orderType.get(j)
								+ "\tGBPUSD: " + priceGBPUSD.get(i) + "\t trades" + trades);
						
						trade(orderType.get(j), priceGBPUSD.get(i));
						

					}

				}

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found: " + file.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	} // main

	public static void trade(String order, double currPrice) {
		
		System.out.print(order + " ok  " + priceStore + " " + formatter.format(totalPips) + "\t\t\n\n");



		switch (order) {
		case "buy":
			priceStore = currPrice;

			break;
		case "sell":

			priceStore = currPrice;
			break;
		case "t/p":
			if (lastOrder.equals("buy")) {

				pips = new BigDecimal(currPrice - priceStore);
				System.out.println("T/P \n" + currPrice + " priceStore: " + priceStore + "\n"

						+ "arithmetic in profit: " + pips + "\n");

				if (pips.compareTo(BigDecimal.ZERO) > 0) {
					// Profit

					BigDecimal profit = totalPips.add(pips);
					BigDecimal balance = new BigDecimal(0);
					MathContext mc = new MathContext(10);
					totalPips = balance.add(profit, mc);
					trades++;

					// System.out.println("PROFIT" + totalPips + " trades:" + trades + " \n\n");

				} else {
					// Loss
					BigDecimal profit = totalPips.subtract(pips);
					BigDecimal balance = new BigDecimal(0);
					MathContext mc = new MathContext(10);
					totalPips = balance.subtract(profit, mc);
					trades++;

					// System.out.println("LOSS\t TotalPIPS:" + totalPips + " trades:" + trades + "
					// \n\n");
				}

			}
			if (lastOrder.equals("sell")) {

				pips = new BigDecimal(currPrice - priceStore);

				if (pips.compareTo(BigDecimal.ZERO) > 0) {
					// Loss

					BigDecimal profit = totalPips.subtract(pips);
					BigDecimal balance = new BigDecimal(0);
					MathContext mc = new MathContext(10);
					totalPips = balance.subtract(profit, mc);
					trades++;

					// System.out.println("LOSS \t" + totalPips + " trades:" + trades + " \n\n");

				} else {
					// Profit

					BigDecimal profit = totalPips.add(pips);
					BigDecimal balance = new BigDecimal(0);
					MathContext mc = new MathContext(10);
					totalPips = balance.add(profit, mc);
					trades++;

					// System.out.println("PROFIT\t TotalPIPS:" + totalPips + " trades:" + trades +
					// " \n\n");
				}

			}

			break;

		case "close":
			if (lastOrder.equals("buy")) {

				pips = new BigDecimal(currPrice - priceStore);
				System.out.println("CLOSE \n" + currPrice + " priceStore: " + priceStore + "\n"

						+ "arithmetic in profit: " + pips + "\n");

				if (pips.compareTo(BigDecimal.ZERO) > 0) {
					// Profit

					BigDecimal profit = totalPips.add(pips);
					BigDecimal balance = new BigDecimal(0);
					MathContext mc = new MathContext(10);
					totalPips = balance.add(profit, mc);
					trades++;

					System.out.println("BUY PROFIT" + totalPips + " trades:" + trades + " \n\n");

				} else {
					// Loss

					BigDecimal profit = totalPips.add(pips);
					BigDecimal balance = new BigDecimal(0);
					MathContext mc = new MathContext(10);
					totalPips = balance.subtract(profit, mc);
					trades++;

					System.out.println("BUY LOSS\t TotalPIPS:" + totalPips + " trades:" + trades + " \n\n");
				}

			}
			if (lastOrder.equals("sell")) {

				pips = new BigDecimal(currPrice - priceStore);
				System.out.println("CLOSE \n" + currPrice + " priceStore: " + priceStore + "\n"

						+ "arithmetic in profit: " + pips + "\n");

				if (pips.compareTo(BigDecimal.ZERO) > 0) {
					// Loss

					BigDecimal profit = totalPips.add(pips);
					BigDecimal balance = new BigDecimal(0);
					MathContext mc = new MathContext(10);
					totalPips = balance.subtract(profit, mc);
					trades++;

					System.out.println("SELL LOSS \t" + totalPips + " trades:" + trades + " \n\n");

				} else {
					// Profit

					BigDecimal profit = totalPips.subtract(pips);
					BigDecimal balance = new BigDecimal(0);
					MathContext mc = new MathContext(10);
					totalPips = balance.add(profit, mc);
					trades++;
 
					System.out.println("SELL PROFIT\t TotalPIPS:" + totalPips + " trades:" + trades + " \n\n");
				}

			}

			break;

		}

		lastOrder = order;

	}

	private static String BigDecimal(double d) {
		// TODO Auto-generated method stub
		return null;
	}

}
