package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;
import okhttp3.*;
import org.json.JSONObject;

// flow start HttpCallFlow correspondingId : 1
@InitiatingFlow
@StartableByRPC
public class HttpCallFlow extends FlowLogic<String> {
    private final ProgressTracker progressTracker = new ProgressTracker();

    private final String correspondingId;
    public HttpCallFlow(String correspondingId){
        this.correspondingId = correspondingId;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public String call() throws FlowException {
        final Request httpRequest = new Request.Builder().url("http://localhost:1024/data/exchange?id="+correspondingId).build();

        String value = null;
        Response httpResponse = null;
        try {
            httpResponse = new OkHttpClient().newCall(httpRequest).execute();
            value = httpResponse.body().string();
        } catch (Exception e) {
            System.out.println("error happen1 -> "+e.getMessage());
        }
        JSONObject jobject = new JSONObject(value);
        String data =jobject.getString("data");
        String address =jobject.getString("address");
        System.out.println("Data received - " + data);

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        String jsonStringTemp = "{\"jsonrpc\": \"2.0\",\"id\": 1,\"method\": \"eth_call\",\"params\": [{\"to\": \"%s\",\"data\": \"%s\"},\"latest\"]}";

        String jsonString = String.format(jsonStringTemp,address, data);
        RequestBody body = RequestBody.create(mediaType,jsonString );
        Request request = new Request.Builder()
                .url("https://rinkeby.infura.io/v3/98079c61ec6a4c029817d276104753d3")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {

            Response response = client.newCall(request).execute();
            System.out.println("Second api called");

            JSONObject infuraObject = new JSONObject(response.body().string());
            System.out.println("JSON created");
            String result =infuraObject.getString("result");
            System.out.println("result - "+ result);

            String status = this.getStatus(result);
            System.out.println("status - "+ status);
            return status;
        } catch (Exception e) {
            System.out.println("error happen -> "+e.getMessage());
        }

        return "0";
    }
    private String getStatus(String hexx){
        String hex = hexx.substring(2);
        String[] stringChunks= hex.split("(?<=\\G.{64})");
        int status=Integer.parseInt(stringChunks[5],16);
        return String.valueOf(status);
    }

    private String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    private String asciiToHex(String asciiStr) {
        char[] chars = asciiStr.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char ch : chars) {
            hex.append(Integer.toHexString((int) ch));
        }

        return hex.toString();
    }
}
