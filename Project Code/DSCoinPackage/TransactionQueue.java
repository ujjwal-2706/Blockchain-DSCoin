package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
    if(numTransactions == 0)
    {
      firstTransaction = transaction;
      lastTransaction = transaction;
      numTransactions++;
    }
    else if(numTransactions ==1)
    {
      lastTransaction = transaction;
      lastTransaction.previous = firstTransaction;
      firstTransaction.next = lastTransaction;
      numTransactions++;
    }
    else
    {
      transaction.previous = lastTransaction;
      lastTransaction.next = transaction;
      lastTransaction = transaction;
      numTransactions++;
    }
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(numTransactions == 0)
    {
      throw new EmptyQueueException();
    }
    else if(numTransactions == 1)
    {
      Transaction first = firstTransaction;
      firstTransaction  = null;
      lastTransaction =  null;
      numTransactions--;
      return first;
    }
    else 
    {
      Transaction first = firstTransaction;
      firstTransaction = firstTransaction.next;
      firstTransaction.previous = null;
      numTransactions--;
      return first; 
    }
  }

  public int size() {
    return numTransactions;
  }
}
