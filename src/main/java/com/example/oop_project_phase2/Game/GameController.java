package com.example.oop_project_phase2.Game;


import com.example.oop_project_phase2.UserManagement.NoUserException;
import com.example.oop_project_phase2.UserManagement.PasswordExeption;
import com.example.oop_project_phase2.UserManagement.SQLhandler;
import com.example.oop_project_phase2.UserManagement.User;
import com.example.oop_project_phase2.card.Card;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.*;

public class GameController {
    public static int preXPH;
    public static int preCoinsH;
    public static int preXPG;
    public static int preCoinsG;
    static public User host1;
    static public User quest1;
    static public User host2;
    static public User quest2;
    static public int round;
    static boolean finish=false;
    public boolean UsersReady = false;
    public static boolean whoStart;
    public static void init(User host,User Guest) {
        host1 = host;
        quest1 = Guest;
        round = 4;
        host1.maxHP = User.getMaxHp(host1.Level);
        quest1.maxHP = User.getMaxHp(quest1.Level);
        host1.hitpoint = host1.maxHP;
        quest1.hitpoint = quest1.maxHP;
        preCoinsH = host1.Coins;
        preXPH = host1.XP;
        preCoinsG = quest1.Coins;
        preXPG = quest1.XP;
        whoStart = whoStart();
        host1.comeInHound = new ArrayList<ArrayList<String>>();
        quest1.comeInHound = new ArrayList<ArrayList<String>>();
        host1.hand = get5CardHand(host1);
        quest1.hand = get5CardHand(quest1);
        host1.timeline = new Card[21];
        quest1.timeline = new Card[21];
        if (whoStart) {
            Game.showHand(host1, quest1);
            host2 = host1;
            quest2 = quest1;
            emptyCell(host2,quest2);
        } else {
            Game.showHand(quest1, host1);
            host2 = quest1;
            quest2 = host1;
            emptyCell(host2,quest2);
        }
    }
//    public void calculategame(){
//            if (whoStart) {
//                while (round > 0) {
//                    Game.timelineInputOutput(host1, quest1);
//                    Game.timelineInputOutput(quest1, host1);
//                }
//            }
//            else {
//                while (round > 0) {
//                    Game.timelineInputOutput(quest1, host1);
//                    Game.timelineInputOutput(host1, quest1);
//                }
//            }
//    }
    public static void endlittleround(){
        User temp;
        temp = host2;
        host2 = quest2;
        quest2 = temp;
        Game.showHand(host2,quest2);
        Game.setTimLineImage(host2,quest2);

    }
    public static void freshhand(){
        round--;
        if (host1.hand.size() < 5 || host1.become6CardInHand || host1.become6CardInHandOneTime) {
            host1.become6CardInHandOneTime = false;
            getNewCardInHand(host1);
        }
        if (quest1.hand.size() < 5 || quest1.become6CardInHand || quest1.become6CardInHandOneTime) {
            getNewCardInHand(quest1);
            quest1.become6CardInHandOneTime = false;
        }
    }
    public static void end4round(Label hitpoinhost,Label hitpointGuest){
        attackUser(host1,quest1,hitpoinhost,hitpointGuest);
        endRound(host1,quest1);
    }
    public static void endGAME(){
        endGame(host2,quest2);
        SQLhandler.updateUser(host2);
        SQLhandler.updateUser(quest2);
    }
    public static ArrayList<Card> get5CardHand(User user)
    {
        ArrayList<Card> answer=new ArrayList<Card>();
        ArrayList<Card> cardThatNormal=SQLhandler.getUsercardsnormal(user);
        ArrayList<Card> special= SQLhandler.getUsercardsspecial(user);
        for (int i=0;i<cardThatNormal.size();i++)
        {
            user.comeInHound.add(new ArrayList<String>());
            user.comeInHound.getLast().add(cardThatNormal.get(i).name);
            user.comeInHound.getLast().add("0");
        }
        for (int i=0;i<special.size();i++)
        {
            user.comeInHound.add(new ArrayList<String>());
            user.comeInHound.getLast().add(special.get(i).name);
            user.comeInHound.getLast().add("0");
        }
        int n=0;
        Random random=new Random();
        int a=random.nextInt(user.comeInHound.size());
        while (n<5)
        {
            if(Integer.parseInt(user.comeInHound.get(a).getLast())==0)
            {
                answer.add(SQLhandler.getCard(user,user.comeInHound.get(a).getFirst()));
                Integer b=Integer.parseInt(user.comeInHound.get(a).getLast());
                b++;
                user.comeInHound.get(a).set(1,b.toString());
                n++;
            }
            else
            {
                Integer b=Integer.parseInt(user.comeInHound.get(a).getLast());
                b--;
                user.comeInHound.get(a).set(1,b.toString());
            }
            a=random.nextInt(user.comeInHound.size());
        }
        answer.get(2).buffInHand(user);
        return answer;
    }
    public static void getNewCardInHand(User user)
    {
        Random random=new Random();
        int a=random.nextInt(user.comeInHound.size());
        boolean getNewCard=false;
        while (!getNewCard)
        {
            if(Integer.parseInt(user.comeInHound.get(a).getLast())==0)
            {
                user.hand.add(SQLhandler.getCard(user,user.comeInHound.get(a).getFirst()));
                Integer b=Integer.parseInt(user.comeInHound.get(a).getLast());
                b++;
                user.comeInHound.get(a).set(1,b.toString());
                getNewCard=true;
            }
            else
            {
                Integer b=Integer.parseInt(user.comeInHound.get(a).getLast());
                b--;
                user.comeInHound.get(a).set(1,b.toString());
            }
            a=random.nextInt(user.comeInHound.size());
        }
        if(user.hand.size()%2==1)
        {
            user.hand.get(2).buffInHand(user);
        }
    }
    public static void attackUser(User host, User guest, Label hitpointHost,Label hitpointGuest)
    {
        Timer timer = new Timer();
        for (int i=0;i<21 && !finish;i++) {
            int a = i + 1;
            TimelineController.reduceHitpoint(host, guest, i);
            int c = host.maxHP - host.hitpoint;
            System.out.println("user host: " + host.Nickname + "  damage:  " + c + "  HitPoint host:  " + host.hitpoint);
            if (host.timeline[i] != null) {
                if (!Objects.equals(host.timeline[i].name, "empty")) {
                    System.out.println("card host block[" + a + "] name:  " + host.timeline[i].name + "  card damage:  " + host.timeline[i].playerDamage);
                } else if (Objects.equals(host.timeline[i].name, "empty") && host.timeline[i].cardReference != null) {
                    System.out.println("card host block[" + a + "] name:  " + host.timeline[i].cardReference.name + "  card damage:  " + host.timeline[i].playerDamage);
                } else {
                    System.out.println("Wall");
                }

            } else {
                System.out.println("card host block[" + a + "] name:  " + "empty" + "  card damage:  " + 0);
            }
            c = guest.maxHP - guest.hitpoint;
            System.out.println("user guest: " + guest.Nickname + "  damage:  " + c + "  HitPoint guest:  " + guest.hitpoint);
            if (guest.timeline[i] != null) {
                if (!Objects.equals(guest.timeline[i].name, "empty")) {
                    System.out.println("card host block[" + a + "] name:  " + guest.timeline[i].name + "  card damage:  " + guest.timeline[i].playerDamage);
                } else if (Objects.equals(guest.timeline[i].name, "empty") && guest.timeline[i].cardReference != null) {
                    System.out.println("card host block[" + a + "] name:  " + guest.timeline[i].cardReference.name + "  card damage:  " + guest.timeline[i].playerDamage);
                } else {
                    System.out.println("Wall");
                }

            } else {
                System.out.println("card guest block[" + a + "] name:  " + "empty" + "  card damage:  " + 0);

            }
            if (host.hitpoint <= 0 || guest.hitpoint <= 0) {
                finish = true;
            }

                    hitpointHost.setText("hitpoint:"+host.hitpoint);
                    hitpointGuest.setText("Guesthitpoint:"+guest.hitpoint);
        }
    }
    public static void endRound(User host,User guest)
    {
        host.become4CardInHand=false;
        host.become6CardInHandOneTime=false;
        host.become6CardInHand=false;
        guest.become4CardInHand=false;
        guest.become6CardInHandOneTime=false;
        guest.become6CardInHand=false;
        round=4;
    }
    public static void endGame(User host,User guest)
    {
        boolean hostwin = false;
        if(host.hitpoint>0)
        {
            hostwin = true;
            if(Gameinit.wager)
            {
                System.out.println("user "+host.Nickname+" win!!");
                System.out.println("user "+host.Nickname+" get "+Gameinit.wagerint+" coin");
                System.out.println("user "+guest.Nickname+" lose!!");
                int a=-Gameinit.wagerint;
                System.out.println("user "+guest.Nickname+"get "+a+" coin");
                host.Coins+=Gameinit.wagerint;
                guest.Coins-=Gameinit.wagerint;
            }
            else
            {
                System.out.println("user "+host.Nickname+" win!!");
                System.out.println("user"+host.Nickname+" get 50 coin");
                host.Coins+=50;
                System.out.println("user"+host.Nickname+" experience increase 60 ");
                host.XP+=60;
                System.out.println("user "+guest.Nickname+" lose!!");
                System.out.println("user"+guest.Nickname+" experience increase 5 ");
                guest.XP+=5;
            }
        }
        else if(guest.hitpoint>0)
        {
            hostwin = false;
            if(Gameinit.wager)
            {
                System.out.println("user "+guest.Nickname+" win!!");
                System.out.println("user "+guest.Nickname+" get "+Gameinit.wagerint+" coin");
                System.out.println("user "+host.Nickname+" lose!!");
                int a=-Gameinit.wagerint;
                System.out.println("user "+host.Nickname+"get "+a+" coin");
                guest.Coins+=Gameinit.wagerint;
                host.Coins-=Gameinit.wagerint;
            }
            else
            {
                System.out.println("user "+guest.Nickname+" win!!");
                System.out.println("user"+guest.Nickname+" get 50 coin");
                guest.Coins+=50;
                System.out.println("user"+guest.Nickname+" experience increase 60 ");
                guest.XP+=60;
                System.out.println("user "+host.Nickname+" lose!!");
                System.out.println("user"+host.Nickname+" experience increase 5 ");
                host.XP+=5;
            }
        }
        int perviousXpHost=host.XP;
        int perviousXpGuest=guest.XP;
        int perviousLevelHost=host.Level;
        int perviousLevelGuest=guest.Level;
        SQLhandler.setnewHistory(host.Username,guest.Username,hostwin,!hostwin,host.Level,guest.Level,host.Coins-preCoinsH,host.XP-preXPH,guest.Coins-preCoinsG,guest.XP-preXPG);
        SQLhandler.setnewHistory(guest.Username,host.Username,!hostwin,hostwin,guest.Level,host.Level,guest.Coins-preCoinsG,guest.XP-preXPG,host.Coins-preCoinsH,host.XP-preXPH);
        host.Level=User.updateLevel(perviousLevelHost,perviousXpHost);
        guest.Level=User.updateLevel(perviousLevelGuest,perviousXpGuest);
        host.XP=User.updateXp(perviousLevelHost,perviousXpHost);
        guest.XP=User.updateXp(perviousLevelGuest,perviousXpGuest);
        finish=false;
    }
    public static void emptyCell(User host,User Guest)
    {
        Random random=new Random();
        int a=random.nextInt(21);
        host.timeline[a]=new Card("empty",0,0,0,0,0,null,null,0);
        Game.cardImage[0][a].setImage(resourceManagement.wall);
        a=random.nextInt(21);
        Guest.timeline[a]=new Card("empty",0,0,0,0,0,null,null,0);
        Game.cardImage[1][a].setImage(resourceManagement.wall);
    }
    public static boolean whoStart()
    {
        Random random=new Random();
        int a=random.nextInt(10);
        if(a<=5)
        {
            return true;
        }
        return false;
    }
}
