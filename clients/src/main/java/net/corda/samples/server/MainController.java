package net.corda.samples.server;

import net.corda.core.contracts.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.fyp.flows.*;

import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import net.corda.fyp.states.ExchangeDetailState;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.scheduling.annotation.Scheduled;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.NoOpProcessor;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Define your API endpoints here.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@Configuration
@RestController
@RequestMapping("/api") // The paths for HTTP requests are relative to this base path.
public class MainController {
    private static final Logger logger = LoggerFactory.getLogger(RestController.class);
    private final CordaRPCOps proxy;
    private final CordaX500Name me;

    public MainController(NodeRPCConnection rpc) {
        this.proxy = rpc.getProxy();
        this.me = proxy.nodeInfo().getLegalIdentities().get(0).getName();

    }

    /** Helpers for filtering the network map cache. */
    public String toDisplayString(X500Name name){
        return BCStyle.INSTANCE.toString(name);
    }

    private boolean isNotary(NodeInfo nodeInfo) {
        return !proxy.notaryIdentities()
                .stream().filter(el -> nodeInfo.isLegalIdentity(el))
                .collect(Collectors.toList()).isEmpty();
    }

    public static class CreateAccountData{
        public String accountName;
    }
    public static class CreateToken{
        public BigDecimal valuation;
        public String symbol;
        public int quantity;
    }
    public static class Exchange{
        public String senderAccount;
        public String correspondingId;
        public Long amount;
        public String symbol;
    }

    private boolean isMe(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().equals(me);
    }

    private boolean isNetworkMap(NodeInfo nodeInfo){
        return nodeInfo.getLegalIdentities().get(0).getName().getOrganisation().equals("Network Map Service");
    }


    @GetMapping(value = "/status", produces = TEXT_PLAIN_VALUE)
    private String status() {
        return "200";
    }

    @GetMapping(value = "/servertime", produces = TEXT_PLAIN_VALUE)
    private String serverTime() {
        return (LocalDateTime.ofInstant(proxy.currentNodeTime(), ZoneId.of("UTC"))).toString();
    }

    @GetMapping(value = "/addresses", produces = TEXT_PLAIN_VALUE)
    private String addresses() {
        return proxy.nodeInfo().getAddresses().toString();
    }

    @GetMapping(value = "/identities", produces = TEXT_PLAIN_VALUE)
    private String identities() {
        return proxy.nodeInfo().getLegalIdentities().toString();
    }

    @GetMapping(value = "/platformversion", produces = TEXT_PLAIN_VALUE)
    private String platformVersion() {
        return Integer.toString(proxy.nodeInfo().getPlatformVersion());
    }

    @GetMapping(value = "/peers", produces = APPLICATION_JSON_VALUE)
    public HashMap<String, List<String>> getPeers() {
        HashMap<String, List<String>> myMap = new HashMap<>();

        // Find all nodes that are not notaries, ourself, or the network map.
        Stream<NodeInfo> filteredNodes = proxy.networkMapSnapshot().stream()
                .filter(el -> !isNotary(el) && !isMe(el) && !isNetworkMap(el));
        // Get their names as strings
        List<String> nodeNames = filteredNodes.map(el -> el.getLegalIdentities().get(0).getName().toString())
                .collect(Collectors.toList());

        myMap.put("peers", nodeNames);
        return myMap;
    }

    @GetMapping(value = "/notaries", produces = TEXT_PLAIN_VALUE)
    private String notaries() {
        return proxy.notaryIdentities().toString();
    }

    @GetMapping(value = "/flows", produces = TEXT_PLAIN_VALUE)
    private String flows() {
        return proxy.registeredFlows().toString();
    }

    @GetMapping(value = "/states", produces = TEXT_PLAIN_VALUE)
    private String states() {
        return proxy.vaultQuery(ContractState.class).getStates().toString();
    }

    @GetMapping(value =  "available-tokens" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> checkAvailableTokens(@RequestParam(value = "symbol") String symbol) {

        try {
            String balance = proxy.startTrackedFlowDynamic(GetAvailableTokens.class ,
                    symbol).getReturnValue().get();
            System.out.println(balance);
            return ResponseEntity.status(HttpStatus.CREATED).body(balance);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping(value =  "account-balance" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> checkAccountBalance(@RequestParam(value = "accountName") String accountName) {
        try {
            String balance = proxy.startTrackedFlowDynamic(QuerybyAccount.class ,
                    accountName).getReturnValue().get();
            System.out.println(balance);
            return ResponseEntity.status(HttpStatus.CREATED).body(balance);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @GetMapping(value =  "check-valuation" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> checkShareValuation(@RequestParam(value = "symbol") String symbol) {
        try {
            BigDecimal valuation = proxy.startTrackedFlowDynamic(GetTokenValuation.class ,
                    symbol).getReturnValue().get();
            System.out.println(valuation);
            return ResponseEntity.status(HttpStatus.CREATED).body(valuation.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value =  "account" , produces =  APPLICATION_JSON_VALUE )
    public ResponseEntity<String> createAccount(@RequestBody CreateAccountData data) {
        try {
            List<Party> emptyList = Collections.<Party>emptyList();
            String msg = proxy.startTrackedFlowDynamic(CreateAndShareAccountFlow.class,data.accountName,emptyList).getReturnValue().get();
            System.out.println(msg);
            return ResponseEntity.status(HttpStatus.CREATED).body(msg);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value =  "token" , produces =  APPLICATION_JSON_VALUE )
    public ResponseEntity<String> createToken(@RequestBody CreateToken data) {
        try {
            proxy.startTrackedFlowDynamic(RealEstateEvolvableFungibleTokenFlow.CreateHouseTokenFlow.class,data.symbol,data.valuation,data.quantity).getReturnValue().get();
            System.out.println("flow created");
            return ResponseEntity.status(HttpStatus.CREATED).body("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value =  "exchange" , produces =  APPLICATION_JSON_VALUE )
    public ResponseEntity<String> exchange(@RequestBody Exchange data) {
        try {
            Boolean res=proxy.startTrackedFlowDynamic(ExchangeShares.class,data.senderAccount,data.amount,data.correspondingId,"1",data.symbol).getReturnValue().get();
            System.out.println("flow created - "+ res);
            return ResponseEntity.status(HttpStatus.CREATED).body("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping(value =  "check-claim" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> checkClaimAvailability(@RequestParam(value = "exchangeId") String exchangeId) {
        try {
            ExchangeDetailState data = proxy.startTrackedFlowDynamic(GetExchangeData.class ,
                    exchangeId).getReturnValue().get();
            if(data==null){
                return ResponseEntity.status(HttpStatus.CREATED).body("0");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("1");
        } catch (Exception e) {
            System.out.println("Error happen - " +e.getMessage());
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
    @GetMapping(value =  "check-refund" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> checkRefundAvailability(@RequestParam(value = "exchangeId") String exchangeId) {
        try {
            ExchangeDetailState data = proxy.startTrackedFlowDynamic(GetExchangeData.class ,
                    exchangeId).getReturnValue().get();
            if(data==null){
                return ResponseEntity.status(HttpStatus.CREATED).body("1");
            }
            System.out.println(data.getCorrespondingId());
            System.out.println(data.getExchangeId());
            System.out.println(data.getSenderAccount());
            if(data.getStatus() == "2"){
                return ResponseEntity.status(HttpStatus.CREATED).body("1");
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("0");
        } catch (Exception e) {
            System.out.println("Error happen - " +e.getMessage());
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @GetMapping(value =  "update-status" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> updateExchangeStatus(@RequestParam(value = "exchangeId") String exchangeId,@RequestParam(value = "status") String status) {
        try {
            proxy.startTrackedFlowDynamic(UpdateExchange.UpdateExchangeInitiator.class ,
                    exchangeId,status).getReturnValue().get();

            return ResponseEntity.status(HttpStatus.CREATED).body("0");
        } catch (Exception e) {
            System.out.println("Error happen - " +e.getMessage());
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }


    @GetMapping(value =  "web3" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> checkWeb3() {
        try {
            String privatekey = "0cf90379972d6ea3098979f143f3ece83d2bbf0800bb2fb2d971115e2d843aa1";
            BigInteger privkey = new BigInteger(privatekey, 16);
            ECKeyPair ecKeyPair = ECKeyPair.create(privkey);
            Credentials credentials = Credentials.create(ecKeyPair);

            Web3jService service = new HttpService("https://ropsten.infura.io/v3/98079c61ec6a4c029817d276104753d3");

            Web3j web3j = Web3j.build(service);

            Counter contract = Counter.load("0x4511e50a040c4d9bcdfe17203fdc3baada9c4c95",web3j,credentials,new DefaultGasProvider());
            System.out.println("Welcome " + credentials.getAddress());
//            contract.inc().send();
            BigInteger val = contract.get().send();
            System.out.println(String.valueOf(val));
            return ResponseEntity.status(HttpStatus.CREATED).body("0");
        } catch (Exception e) {
            System.out.println("Error happen - " +e.getMessage());
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @GetMapping(value =  "process" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> process() {
        try {
            OkHttpClient client = new OkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:1024/data").newBuilder();
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body());
            okhttp3.ResponseBody statusArray = response.body();
            JSONArray array = new JSONArray(response.body().string());
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                System.out.println(object.getString("cordaAccount"));
            }


            return ResponseEntity.status(HttpStatus.CREATED).body("0");
        } catch (Exception e) {
            System.out.println("Error happen - " +e.getMessage());
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

    @GetMapping(value =  "hex" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> processhex() {
        try {
            String hexx = "0x00000000000000000000000000000000000000000000000000000000000000010000000000000000000000002e81f5ff999145d13eefae00632dcea1714358e700000000000000000000000000000000000000000000000000000000000000e000000000000000000000000000000000000000000000000000470de4df82000000000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000001000000000000000000000000000000000000000000000000000000000000012000000000000000000000000000000000000000000000000000000000000000056e7572616a000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000d636c61696d526563656976656400000000000000000000000000000000000000";
            String hex = hexx.substring(2);
            String[] stringChunks= hex.split("(?<=\\G.{64})");
            System.out.println(this.hexToAscii(stringChunks[10]));

            BigInteger bigNumber = new BigInteger(stringChunks[3] , 16);
            System.out.println(bigNumber);

            int decimal=Integer.parseInt(stringChunks[4],16);
            System.out.println(decimal);

            return ResponseEntity.status(HttpStatus.CREATED).body("0");
        } catch (Exception e) {
            System.out.println("Error happen - " +e.getMessage());
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
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


    @Scheduled(initialDelay = 1000, fixedRate = 120000)
    public void run() {
        try {
            BigDecimal valuation = proxy.startTrackedFlowDynamic(GetTokenValuation.class,
                    "fyp").getReturnValue().get();
            System.out.println(valuation);
            logger.info("Current time is :: " + Calendar.getInstance().getTime());
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder urlBuilder = HttpUrl.parse("http://localhost:1024/data").newBuilder();
            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println(response.body());
            JSONArray array = new JSONArray(response.body().string());
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String sender = object.getString("sender");
                String cordaAccount = object.getString("cordaAccount");
                String exchangeId = object.getString("exchangeId");
                Long numbrOfShares = object.getLong("numbrOfShares");
                System.out.println("corda account- "+cordaAccount + " sender- "+sender+" exchange id- "+exchangeId + " number of shares- "+numbrOfShares);
                Boolean res=proxy.startTrackedFlowDynamic(ExchangeShares.class,cordaAccount,numbrOfShares,exchangeId,"1","fyp").getReturnValue().get();
                System.out.println("flow created - "+ res);
                TimeUnit.SECONDS.sleep(60);
            }
        }catch(Exception e){
            System.out.println("Error happen - " + e.getMessage());
        }
    }

}
