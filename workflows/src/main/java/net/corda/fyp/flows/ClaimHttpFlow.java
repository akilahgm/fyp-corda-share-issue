package net.corda.fyp.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.utilities.ProgressTracker;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

// flow start ClaimHttpFlow correspondingId : 1
@InitiatingFlow
@StartableByRPC
public class ClaimHttpFlow extends FlowLogic<Boolean> {
    private final ProgressTracker progressTracker = new ProgressTracker();

    private final String correspondingId;
    public ClaimHttpFlow(String correspondingId){
        this.correspondingId = correspondingId;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public Boolean call() throws FlowException {
        final Request httpRequest = new Request.Builder().url("http://localhost:1024/data/claim?id="+correspondingId).build();

        String value = null;
        Response httpResponse = null;
        try {
            httpResponse = new OkHttpClient().newCall(httpRequest).execute();
            value = httpResponse.body().string();
        } catch (IOException e) {
            System.out.println("error happen -> "+e.getMessage());
            return false;
        }
        JSONObject jobject = new JSONObject(value);
        String data =jobject.getString("data");
        System.out.println("Data received - "+data);

        String jsonStringTemp = "{\"jsonrpc\": \"2.0\",\"id\": 1,\"method\": \"eth_sendRawTransaction\",\"params\": [\"%s\"]}";
        String jsonString = String.format(jsonStringTemp, data);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
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
            System.out.println("JSON created" + infuraObject.toString());
            String result =infuraObject.getString("result");
            System.out.println("result - "+ result);

            return true;
        } catch (Exception e) {
            System.out.println("error happen -> "+e.getMessage());
            return false;
        }
    }
}
