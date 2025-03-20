package blockchain;
import com.google.gson.GsonBuilder;
import java.util.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


import java.security.Security;
import java.util.ArrayList;
public class BlockChain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();//list of all unspent transactions.
	public static Wallet walletA;
	public static Wallet walletB;
	private static int difficulty = 3;
	public static final float minimumTransaction = 0.1f;
	
	//main storage of coins
	public static Transaction genesisTransaction;
	
	public static void main(String[] args)
	{
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		//create two wallets 
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();
		
		//source wallet
		Wallet coinBase = new Wallet();
		
		//provider with the first transaction 
		genesisTransaction = new Transaction(coinBase.publicKey,walletA.publicKey,100f,null);
		genesisTransaction.generateSignature(coinBase.privateKey);
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId));
		UTXOs.put(genesisTransaction.outputs.get(0).id,genesisTransaction.outputs.get(0));
		System.out.println("Creating and Mining Genesis block... ");
		
		
		System.out.println("private and public key!!");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		Transaction transaction = new Transaction(walletA.publicKey,walletB.publicKey,5,null);
		transaction.generateSignature(walletA.privateKey);
		System.out.println("transaction verification: "+transaction.signatureVerification());
		
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		//testing
				Block block1 = new Block(genesis.hash);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
				block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
				addBlock(block1);
				System.out.println("\nWalletA's balance is: " + walletA.getBalance());
				System.out.println("WalletB's balance is: " + walletB.getBalance());
				
				Block block2 = new Block(block1.hash);
				System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
				block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
				addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		isChainValid();
		
		/*
		//System.out.println("ello");
		/*
		 // it was a test!!
		Block gennesisBlock  = new Block("This is block 1","0");
		System.out.println("block 1: "+gennesisBlock.hash);
		
		Block secondBlock = new Block("this is second Block",gennesisBlock.hash);
		System.out.println("block 2: "+secondBlock.hash);
		
		Block thirdBlock = new Block("this is my third block mate!!",secondBlock.hash);
		System.out.println("block 3: "+thirdBlock.hash);
		
		blockchain.add(new Block("This is block 1","0"));
		blockchain.get(0).mineBlock(difficulty);
		blockchain.add(new Block("this is block 2",blockchain.get(blockchain.size()-1).hash));
		blockchain.get(1).mineBlock(difficulty);
		blockchain.add(new Block("this is block 3",blockchain.get(blockchain.size()-1).hash));
		blockchain.get(2).mineBlock(difficulty);
	//	blockchain.get(0).previousHash="ancdf"; test to make it hash wrong !!
		if(isChainValid()) {
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
	
			System.out.println(blockchainJson);
			}
			*/
		System.out.println("int he chain");	
							
	}
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	
	public static boolean isChainValid()
	{
		
		
		Block currentBlock ;
		Block previousBlock;
		String hashTarget = new String (new char[difficulty]).replace('\0', '0');
		
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		
		for(int i=1;i< blockchain.size();i++)
		{
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			if(!currentBlock.hash.equals(currentBlock.caclculateHash()))
			{
				System.out.println(i+"->"+(i-1)+"not equal");
				System.out.println("block id: "+i);
				return false;
			}
			
			if(!previousBlock.hash.equals(previousBlock.caclculateHash()))
			{
				
				System.out.println(i+"->"+(i-1)+"not equal");
				System.out.println("block id: "+(i-1));
				return false;
			}
			if(!currentBlock.hash.substring(0,difficulty).equals(hashTarget))
			{
				System.out.println("block is not mined!!"); // if first few digits of the hash is not 0000..
				return false;
			}
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.signatureVerification()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(currentTransaction.getInputValue() != currentTransaction.getOutputValues()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}

				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain is valid");
		return true;
	}
	}


