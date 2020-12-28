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
public class Counter extends Contract {
    public static final String BINARY = "{\r\n"
            + "\t\"linkReferences\": {},\r\n"
            + "\t\"object\": \"606060405260008055341561001357600080fd5b610142806100226000396000f300606060405260043610610062576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306661abd14610067578063371303c0146100905780636d4ce63c146100a5578063b3bcfa82146100ce575b600080fd5b341561007257600080fd5b61007a6100e3565b6040518082815260200191505060405180910390f35b341561009b57600080fd5b6100a36100e9565b005b34156100b057600080fd5b6100b86100fb565b6040518082815260200191505060405180910390f35b34156100d957600080fd5b6100e1610104565b005b60005481565b60016000808282540192505081905550565b60008054905090565b600160008082825403925050819055505600a165627a7a72305820db595e2e6edd79e2478f48a4ad8c04d7f1ba261c59605d40fb956ab2770368680029\",\r\n"
            + "\t\"opcodes\": \"PUSH1 0x60 PUSH1 0x40 MSTORE PUSH1 0x0 DUP1 SSTORE CALLVALUE ISZERO PUSH2 0x13 JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST PUSH2 0x142 DUP1 PUSH2 0x22 PUSH1 0x0 CODECOPY PUSH1 0x0 RETURN STOP PUSH1 0x60 PUSH1 0x40 MSTORE PUSH1 0x4 CALLDATASIZE LT PUSH2 0x62 JUMPI PUSH1 0x0 CALLDATALOAD PUSH29 0x100000000000000000000000000000000000000000000000000000000 SWAP1 DIV PUSH4 0xFFFFFFFF AND DUP1 PUSH4 0x6661ABD EQ PUSH2 0x67 JUMPI DUP1 PUSH4 0x371303C0 EQ PUSH2 0x90 JUMPI DUP1 PUSH4 0x6D4CE63C EQ PUSH2 0xA5 JUMPI DUP1 PUSH4 0xB3BCFA82 EQ PUSH2 0xCE JUMPI JUMPDEST PUSH1 0x0 DUP1 REVERT JUMPDEST CALLVALUE ISZERO PUSH2 0x72 JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST PUSH2 0x7A PUSH2 0xE3 JUMP JUMPDEST PUSH1 0x40 MLOAD DUP1 DUP3 DUP2 MSTORE PUSH1 0x20 ADD SWAP2 POP POP PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 RETURN JUMPDEST CALLVALUE ISZERO PUSH2 0x9B JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST PUSH2 0xA3 PUSH2 0xE9 JUMP JUMPDEST STOP JUMPDEST CALLVALUE ISZERO PUSH2 0xB0 JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST PUSH2 0xB8 PUSH2 0xFB JUMP JUMPDEST PUSH1 0x40 MLOAD DUP1 DUP3 DUP2 MSTORE PUSH1 0x20 ADD SWAP2 POP POP PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 RETURN JUMPDEST CALLVALUE ISZERO PUSH2 0xD9 JUMPI PUSH1 0x0 DUP1 REVERT JUMPDEST PUSH2 0xE1 PUSH2 0x104 JUMP JUMPDEST STOP JUMPDEST PUSH1 0x0 SLOAD DUP2 JUMP JUMPDEST PUSH1 0x1 PUSH1 0x0 DUP1 DUP3 DUP3 SLOAD ADD SWAP3 POP POP DUP2 SWAP1 SSTORE POP JUMP JUMPDEST PUSH1 0x0 DUP1 SLOAD SWAP1 POP SWAP1 JUMP JUMPDEST PUSH1 0x1 PUSH1 0x0 DUP1 DUP3 DUP3 SLOAD SUB SWAP3 POP POP DUP2 SWAP1 SSTORE POP JUMP STOP LOG1 PUSH6 0x627A7A723058 KECCAK256 0xdb MSIZE 0x5e 0x2e PUSH15 0xDD79E2478F48A4AD8C04D7F1BA261C MSIZE PUSH1 0x5D BLOCKHASH CREATE2 SWAP6 PUSH11 0xB277036868002900000000 \",\r\n"
            + "\t\"sourceMap\": \"27:372:0:-;;;71:1;51:21;;27:372;;;;;;;;;;;;;;\"\r\n"
            + "}";

    public static final String FUNC_COUNT = "count";

    public static final String FUNC_INC = "inc";

    public static final String FUNC_GET = "get";

    public static final String FUNC_DEC = "dec";

    @Deprecated
    protected Counter(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Counter(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Counter(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Counter(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> count() {
        final Function function = new Function(FUNC_COUNT,
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

    public RemoteFunctionCall<BigInteger> get() {
        final Function function = new Function(FUNC_GET,
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

    @Deprecated
    public static Counter load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Counter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Counter(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Counter load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Counter(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Counter load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Counter(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Counter> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Counter.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Counter> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Counter.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<Counter> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Counter.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Counter> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Counter.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}

