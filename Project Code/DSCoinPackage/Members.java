package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;

public class Members
 {

  public String UID;
  public ArrayList<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans = new Transaction[100];

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
    Pair<String, TransactionBlock> firstCoin = mycoins.get(0);
    mycoins.remove(0);
    Transaction firstTransaction = new Transaction();
    firstTransaction.coinID = firstCoin.get_first();
    firstTransaction.coinsrc_block = firstCoin.get_second();
    firstTransaction.Source = this;
    int i=0;
    while(true)
    {
      if(DSobj.memberlist[i].UID.equals(destUID))
      {
        break;
      }
      else 
      {
        i++;
      }        
    }
    firstTransaction.Destination = DSobj.memberlist[i];
    for(int j = 0; j <in_process_trans.length;j++)
    {
      if(in_process_trans[i] == null)
      {
        in_process_trans[i] = firstTransaction;
        break;
      }
    }
    DSobj.pendingTransactions.AddTransactions(firstTransaction);
  }


  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
    Pair<List<Pair<String, String>>, List<Pair<String, String>>> result = new Pair<List<Pair<String, String>>, List<Pair<String, String>>>(null, null);
    TransactionBlock traverse = DSObj.bChain.lastBlock;
    List<TransactionBlock> blockTraverse = new ArrayList<TransactionBlock>();// To move backward, we keep track
    while(traverse != null)
    {
      boolean foundTransaction = false;
      blockTraverse.add(traverse);
      for (int k=0; k < traverse.trarray.length;k++)
      {
        if(traverse.trarray[k] == tobj)
        {
          foundTransaction = true;
          break;
        }
      }
      if(foundTransaction)
      {
        break;
      }
      else
      {
        traverse = traverse.previous;
      }
    }
    if(traverse == null)
    {
      throw new MissingTransactionException();
    }
    else // traverse is the transactionBlock containing tObj
    {
      result.first = traverse.siblingPath(tobj);
      List<Pair<String,String>> blockPath = new ArrayList<Pair<String,String>>();
      if(traverse.previous != null)
      {
        Pair<String,String> element1 = new Pair<String,String>(traverse.previous.dgst,null);
        blockPath.add(element1);
        for(int w = blockTraverse.size()-1;w>=0;w--)
        {
          Pair<String,String> element = new Pair<String,String>(blockTraverse.get(w).dgst,blockTraverse.get(w).previous.dgst + "#" + blockTraverse.get(w).trsummary + "#" + blockTraverse.get(w).nonce );
          blockPath.add(element);
        }
      }
      else 
      {
        Pair<String,String> element1 = new Pair<String,String>("DSCoin",null);
        blockPath.add(element1);
        Pair<String,String> element2 = new Pair<String,String>(traverse.dgst,"DSCoin" + "#" + traverse.trsummary+ "#" + traverse.nonce);
        blockPath.add(element2);
        for(int w = blockTraverse.size()-2;w>=0;w--)
        {
          Pair<String,String> element = new Pair<String,String>(blockTraverse.get(w).dgst,blockTraverse.get(w).previous.dgst + "#" + blockTraverse.get(w).trsummary + "#" + blockTraverse.get(w).nonce );
          blockPath.add(element);
        } 
      }
      result.second=  blockPath;
      for(int a=0; a < in_process_trans.length;a++)
      {
        if(in_process_trans[a] == tobj)
        {
          in_process_trans[a] = null;
          break;
        }
      }
      
      Pair<String, TransactionBlock> destinationCoin = new Pair<String, TransactionBlock>(null,null);
      destinationCoin.second = traverse;
      destinationCoin.first = tobj.coinID;
      List<Pair<String, TransactionBlock>> destiny = tobj.Destination.mycoins;
      int h=0;
      while(h < destiny.size())
      {
        if(Integer.parseInt(destiny.get(h).first) > Integer.parseInt(tobj.coinID))
        {
          break;
        }
        else
        {
          h++;
        }
      }
      destiny.add(h, destinationCoin);
      return result;
    }
  }

  public void MineCoin(DSCoin_Honest DSObj)  {
    Transaction[] Queue = new Transaction[DSObj.bChain.tr_count];
    int nullPosition = 0;
    while(nullPosition < DSObj.bChain.tr_count-1)
    {
      try
      {
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        TransactionBlock sourceBlock = t.coinsrc_block;
        boolean isvalid = false; // To check the validity of transaction
        if(sourceBlock == null)
        {
          isvalid =true;
        }
        else
        { 
          for(int i=0;i < sourceBlock.trarray.length;i++)
          {
            if(t.coinID.equals(sourceBlock.trarray[i].coinID))
            {
              if(t.Source.UID.equals(sourceBlock.trarray[i].Destination.UID))
              {
                isvalid = true;
              }
              else
              {
                break;
              }
            }
          }
          if(!isvalid)
          {
            
          }
          else
          {
            TransactionBlock traverse = DSObj.bChain.lastBlock;
            while(traverse != sourceBlock)
            {
              for(int j=0; j < traverse.trarray.length;j++)
              {
                if(t.coinID.equals(traverse.trarray[j].coinID))
                {
                  isvalid = false;
                  break;
                }
              }
              traverse = traverse.previous;
            }
          }
        }
        if(isvalid)  // isvalid will tell whether the transaction is valid or not
        {
          for(int k=0; k < nullPosition;k++)
          {
            if(Queue[k].coinID.equals(t.coinID))
            {
              isvalid = false;
              break;
            }
          }
          if(isvalid)
          {
            Queue[nullPosition] = t;
            nullPosition++;
          }
        }
      }
      catch(Exception e)
      {
        return ;
      }
    }
    // Now we have constructed the trarray(Queue) till second last element
    Transaction minerReward = new Transaction();
    minerReward.coinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
    minerReward.Destination = this;
    Queue[nullPosition] = minerReward;
    TransactionBlock tB = new TransactionBlock(Queue);
    DSObj.bChain.InsertBlock_Honest(tB);
    DSObj.latestCoinID  = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
    Pair<String, TransactionBlock> addition = new Pair<String,TransactionBlock>(minerReward.coinID,tB);
    this.mycoins.add(this.mycoins.size(),addition);
  }  

  public void MineCoin(DSCoin_Malicious DSObj) {
    Transaction[] Queue = new Transaction[DSObj.bChain.tr_count];
    int nullPosition = 0;
    while(nullPosition < DSObj.bChain.tr_count-1)
    {
      try
      {
        Transaction t = DSObj.pendingTransactions.RemoveTransaction();
        TransactionBlock sourceBlock = t.coinsrc_block;
        boolean isvalid = false;
        if(sourceBlock == null)
        {
          isvalid =true;
        }
        else
        { 
          for(int i=0;i < sourceBlock.trarray.length;i++)
          {
            if(t.coinID.equals(sourceBlock.trarray[i].coinID))
            {
              if(t.Source.UID.equals(sourceBlock.trarray[i].Destination.UID))
              {
                isvalid = true;
              }
              else
              {
                break;
              }
            }
          }
          if(!isvalid)
          {
            
          }
          else
          {
            TransactionBlock traverse = DSObj.bChain.FindLongestValidChain();
            while(traverse != sourceBlock)
            {
              for(int j=0; j < traverse.trarray.length;j++)
              {
                if(t.coinID.equals(traverse.trarray[j].coinID))
                {
                  isvalid = false;
                  break;
                }
              }
              traverse = traverse.previous;
            }
          }
        }
        if(isvalid)  // isvalid will tell whether the transaction is valid or not
        {
          for(int k=0; k < nullPosition;k++)
          {
            if(Queue[k].coinID.equals(t.coinID))
            {
              isvalid = false;
              break;
            }
          }
          if(isvalid)
          {
            Queue[nullPosition] = t;
            nullPosition++;
          }
        }
      }
      catch(Exception e)
      {
        return;
      }
    }
    // Now we have constructed the trarray(Queue) till second last element
    Transaction minerReward = new Transaction();
    minerReward.coinID = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
    minerReward.Destination = this;
    Queue[nullPosition] = minerReward;
    TransactionBlock tB = new TransactionBlock(Queue);
    DSObj.bChain.InsertBlock_Malicious(tB);
    DSObj.latestCoinID  = Integer.toString(Integer.parseInt(DSObj.latestCoinID) + 1);
    Pair<String, TransactionBlock> addition = new Pair<String,TransactionBlock>(minerReward.coinID,tB);
    this.mycoins.add(this.mycoins.size(),addition);
  }  







  // Just checking 
  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
    Pair<String, TransactionBlock> firstCoin = mycoins.get(0);
    mycoins.remove(0);
    Transaction firstTransaction = new Transaction();
    firstTransaction.coinID = firstCoin.get_first();
    firstTransaction.coinsrc_block = firstCoin.get_second();
    firstTransaction.Source = this;
    int i=0;
    while(true)
    {
      if(DSobj.memberlist[i].UID.equals(destUID))
      {
        break;
      }
      else 
      {
        i++;
      }        
    }
    firstTransaction.Destination = DSobj.memberlist[i];
    for(int j = 0; j <in_process_trans.length;j++)
    {
      if(in_process_trans[i] == null)
      {
        in_process_trans[i] = firstTransaction;
        break;
      }
    }
    DSobj.pendingTransactions.AddTransactions(firstTransaction);
  }
}






