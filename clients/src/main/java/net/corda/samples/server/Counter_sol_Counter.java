package net.corda.samples.server;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.5.16.
 */
@SuppressWarnings("rawtypes")
public class Counter_sol_Counter extends Contract {
    public static final String BINARY = "60806040526000600060005090905534801561001b5760006000fd5b50610021565b610233806100306000396000f3fe60806040523480156100115760006000fd5b50600436106100515760003560e01c806306661abd14610057578063371303c0146100755780636d4ce63c1461007f578063b3bcfa821461009d57610051565b60006000fd5b61005f6100a7565b60405161006c9190610118565b60405180910390f35b61007d6100b0565b005b6100876100d1565b6040516100949190610118565b60405180910390f35b6100a56100e3565b005b60006000505481565b60016000600082828250546100c59190610134565b9250508190909055505b565b600060006000505490506100e0565b90565b60016000600082828250546100f8919061018b565b9250508190909055505b566101fc565b610111816101c0565b82525b5050565b600060208201905061012d6000830184610108565b5b92915050565b600061013f826101c0565b915061014a836101c0565b9250827fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff0382111561017f5761017e6101cb565b5b82820190505b92915050565b6000610196826101c0565b91506101a1836101c0565b9250828210156101b4576101b36101cb565b5b82820390505b92915050565b60008190505b919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b565bfea264697066735822122062ade49d2716c30020cf826354c5b3ddd7db8a0d94115ca3fd483ce371ce1b1664736f6c63430008000033";

    public static final String FUNC_COUNT = "count";

    public static final String FUNC_DEC = "dec";

    public static final String FUNC_GET = "get";

    public static final String FUNC_INC = "inc";

    @Deprecated
    protected Counter_sol_Counter(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Counter_sol_Counter(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Counter_sol_Counter(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Counter_sol_Counter(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> count() {
        final Function function = new Function(FUNC_COUNT,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> dec() {
        final Function function = new Function(
                FUNC_DEC,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> get() {
        final Function function = new Function(FUNC_GET,
                Arrays.<Type>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> inc() {
        final Function function = new Function(
                FUNC_INC,
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static Counter_sol_Counter load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter_sol_Counter(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Counter_sol_Counter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter_sol_Counter(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Counter_sol_Counter load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Counter_sol_Counter(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Counter_sol_Counter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Counter_sol_Counter(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Counter_sol_Counter> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Counter_sol_Counter.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Counter_sol_Counter> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Counter_sol_Counter.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Counter_sol_Counter> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Counter_sol_Counter.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Counter_sol_Counter> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Counter_sol_Counter.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
