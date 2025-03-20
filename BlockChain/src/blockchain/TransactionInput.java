package blockchain;

public class TransactionInput {
	public String transactionOutputId;
	public TransactionOutput UTXO; // contains unspent transaction outputs
	
	public TransactionInput(String transactionOutputId)
	{
		this.transactionOutputId = transactionOutputId;
		
	}

}
