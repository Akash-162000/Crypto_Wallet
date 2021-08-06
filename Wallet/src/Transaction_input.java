
public class Transaction_input {
	public String txn_output_id;
	public Transaction_output unspent_txn_op;
	
	public Transaction_input(String txn_output_id)
	{
		this.txn_output_id = txn_output_id;
	}

}
