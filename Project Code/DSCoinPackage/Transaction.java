package DSCoinPackage;

public class Transaction {

  public String coinID;
  public Members Source;
  public Members Destination;
  public TransactionBlock coinsrc_block;

  public Transaction next;// Next pointer for transaction queue
  public Transaction previous; // Previous pointer for transaction queue
}
