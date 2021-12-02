package DSCoinPackage;
import HelperClasses.Pair;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
    String coin1 = "100000";
    int coinUsed = 0;
    int member = DSObj.memberlist.length;
    Members moderator = new Members();
    moderator.UID = "Moderator";
    while(coinUsed < coinCount)
    {
      Transaction[] tarray = new Transaction[DSObj.bChain.tr_count];
      for(int i=0; i < tarray.length;i++)
      {
        Transaction t = new Transaction();
        t.coinID = coin1;
        t.Source = moderator;
        t.Destination = DSObj.memberlist[i%member];
        tarray[i] = t;
        coin1 = Integer.toString(Integer.parseInt(coin1) +1);
        coinUsed++;
      }
      TransactionBlock tB = new TransactionBlock(tarray);
      DSObj.bChain.InsertBlock_Honest(tB);
      for (int k = 0; k < tarray.length;k++)
      {
        Pair<String,TransactionBlock> pair = new Pair<String,TransactionBlock>(tarray[k].coinID,tB); 
        DSObj.memberlist[k%member].mycoins.add(pair);
      }
    }
    DSObj.latestCoinID = Integer.toString(Integer.parseInt(coin1) -1);
  }
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
    String coin1 = "100000";
    int coinUsed = 0;
    Members moderator = new Members();
    int member=DSObj.memberlist.length;
    moderator.UID = "Moderator";
    while(coinUsed < coinCount)
    {
      Transaction[] tarray = new Transaction[DSObj.bChain.tr_count];
      for(int i=0; i < tarray.length;i++)
      {
        Transaction t = new Transaction();
        t.coinID = coin1;
        t.Source = moderator;
        t.Destination = DSObj.memberlist[i%member];
        tarray[i] = t;
        coin1 = Integer.toString(Integer.parseInt(coin1) +1);
        coinUsed++;
      }
      TransactionBlock tB = new TransactionBlock(tarray);
      DSObj.bChain.InsertBlock_Malicious(tB);
      for (int k = 0; k < tarray.length;k++)
      {
        Pair<String,TransactionBlock> pair = new Pair<String,TransactionBlock>(tarray[k].coinID,tB); 
        DSObj.memberlist[k%member].mycoins.add(pair);
      }
    }
    DSObj.latestCoinID = Integer.toString(Integer.parseInt(coin1) -1);
  }
}
