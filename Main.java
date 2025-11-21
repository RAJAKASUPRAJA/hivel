import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {

    
    static String readFile(String filename) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null)
            sb.append(line).append("\n");
        br.close();
        return sb.toString();
    }

    static List<Integer> getPointKeys(String json) {
        List<Integer> keys = new ArrayList<>();
        int i = 0;
        while ((i = json.indexOf("\"", i)) >= 0) {
            int j = json.indexOf("\"", i + 1);
            if (j < 0) break;

            String key = json.substring(i + 1, j);

            
            if (key.matches("\\d+"))
                keys.add(Integer.parseInt(key));

            i = j + 1;
        }
        Collections.sort(keys);
        return keys;
    }

    
    static String getScopedValue(String json, String parentKey, String childKey) {

        int p = json.indexOf("\"" + parentKey + "\"");
        if (p < 0) return null;

        int o = json.indexOf("{", p);
        int c = json.indexOf("}", o);
        if (o < 0 || c < 0) return null;

        String block = json.substring(o + 1, c);

        int ck = block.indexOf("\"" + childKey + "\"");
        if (ck < 0) return null;

        int colon = block.indexOf(":", ck) + 1;

        while (colon < block.length() &&
                (block.charAt(colon) == ' ' || block.charAt(colon) == '"'))
            colon++;

        int e = colon;
        while (e < block.length() &&
                block.charAt(e) != '"' &&
                block.charAt(e) != ',' &&
                block.charAt(e) != '\n' &&
                block.charAt(e) != '\r')
            e++;

        return block.substring(colon, e).trim();
    }

    
    static double[] solve(double[][] A, double[] Y) {
        int n = Y.length;

        for (int i = 0; i < n; i++) {

            double pivot = A[i][i];
            if (pivot == 0) {
                for (int r = i + 1; r < n; r++) {
                    if (A[r][i] != 0) {
                        double[] tm = A[i];
                        A[i] = A[r];
                        A[r] = tm;
                        double tv = Y[i];
                        Y[i] = Y[r];
                        Y[r] = tv;
                        pivot = A[i][i];
                        break;
                    }
                }
            }

            for (int r = i + 1; r < n; r++) {
                double factor = A[r][i] / pivot;
                Y[r] -= factor * Y[i];

                for (int c = i; c < n; c++)
                    A[r][c] -= factor * A[i][c];
            }
        }

        double[] ans = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            ans[i] = Y[i];
            for (int j = i + 1; j < n; j++)
                ans[i] -= A[i][j] * ans[j];
            ans[i] /= A[i][i];
        }

        return ans;
    }

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter input file name (e.g. test1.json or test2.json): ");
        String filename = sc.nextLine().trim();

        String json = readFile(filename);

        
        int pos = json.indexOf("\"k\"");
        pos = json.indexOf(":", pos) + 1;
        while (json.charAt(pos) == ' ' || json.charAt(pos) == '"') pos++;
        int e = pos;
        while (e < json.length() && Character.isDigit(json.charAt(e))) e++;
        int k = Integer.parseInt(json.substring(pos, e));

        
        List<Integer> pts = getPointKeys(json);

        
        double[][] A = new double[k][k];
        double[] Y = new double[k];

        for (int i = 0; i < k; i++) {

            int x = pts.get(i);

            String baseStr = getScopedValue(json, String.valueOf(x), "base");
            String valStr  = getScopedValue(json, String.valueOf(x), "value");

            int base = Integer.parseInt(baseStr);
            BigInteger yBI = new BigInteger(valStr, base);
            Y[i] = yBI.doubleValue();

            long pow = 1;
            for (int j = 0; j < k; j++) {
                A[i][j] = pow;
                pow *= x;
            }
        }

        double[] coeff = solve(A, Y);

        System.out.println("\nConstant term (C₀) = " + Math.round(coeff[0]));
    }
}









