package net.corda.samples.server;

import net.corda.core.contracts.*;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.node.NodeInfo;
import net.corda.fyp.flows.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import net.corda.fyp.states.ExchangeDetailState;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

//import com.fasterxml.jackson.core.*;


import java.util.stream.Collectors;
import java.util.stream.Stream;


import static net.corda.finance.workflows.GetBalances.getCashBalances;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * Define your API endpoints here.
 */
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

    @GetMapping(value =  "exchange" , produces =  TEXT_PLAIN_VALUE )
    public ResponseEntity<String> getExchangeData(@RequestParam(value = "correspondingId") String correspondingId) {
        try {
            ExchangeDetailState data = proxy.startTrackedFlowDynamic(GetExchangeData.class ,
                    correspondingId).getReturnValue().get();
            if(data==null){
                return ResponseEntity.status(HttpStatus.CREATED).body("false");
            }
            System.out.println(data.getCorrespondingId());
            System.out.println(data.getExchangeId());
            System.out.println(data.getSenderAccount());
            return ResponseEntity.status(HttpStatus.CREATED).body("true");
        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            System.out.println("Error happen - " +e.getMessage());
            throw new HttpServerErrorException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }

//    @Scheduled(initialDelay = 1000, fixedRate = 10000)
//    public void run() {
//        logger.info("Current time is :: " + Calendar.getInstance().getTime());
//    }

}