import org.dreambot.api.Client;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.dialogues.Dialogues;
import org.dreambot.api.methods.map.Area;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.skills.Skill;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.wrappers.interactive.NPC;
import org.dreambot.api.wrappers.widgets.message.Message;

import java.awt.*;


@ScriptManifest(name = "Fly Fisher Beta", description = "Gets level required for fishing sharks by fly fishing at Barbarian Village (F2P)", author = "Brotato",
        version = 1.0, category = Category.FISHING, image = "")
public class flyFisher extends AbstractScript {
    State state;

    String s;
    Area fly = new Area(3101, 3434, 3110, 3421);


    boolean isFishing = false;


    @Override // Infinite loop
    public int onLoop() {

        switch (getState()) {
            case STOP:
                stop();
                break;
            case LOGOUT:
                log("Target level reached -- logging out.");
                getTabs().logout();
                break;
            case LFSPOT:
                log("Looking for fishing spot...");
                NPC fishSpot = getNpcs().closest(f -> f != null && f.getName().contentEquals("Rod Fishing spot") && fly.contains(f));
                if (fishSpot !=null){
                    fishSpot.interact("Lure");

                    isFishing = true;
                    log("Found a spot - boolean is true");

                } else {
                    isFishing = false;
                    sleep(5000);
                }
                break;
            case FISHING:
                log("isF is: " + isFishing);
                s = "Fishing";

                Dialogues d = getDialogues();
                while (d.inDialogue()) {
                    if (d.canContinue()) {
                        d.spaceToContinue();
                    }
                }
                if (!getLocalPlayer().isAnimating()){
                    isFishing = false;
                    break;
                }

                if (Inventory.isFull()) {
                    log("Inventory full -- dropping shrimps.");
                    Inventory.dropAll("Raw salmon", "Raw trout");
                    isFishing = false;

                } else {
                    sleep(5000, 8000);
                }
                break;




        }
        return 123;
    }

    private enum State {
        STOP, LOGOUT, FISHING, LFSPOT


    }

    private State getState() {
        if (!Client.isLoggedIn()) {
            state = State.STOP;
        } else if (getSkills().getRealLevel(Skill.FISHING) >= 82) {
            state = State.LOGOUT;
        } else if (isFishing && fly.contains(getLocalPlayer())){
            state = State.FISHING;
        } else if (!getLocalPlayer().isAnimating() && fly.contains(getLocalPlayer())){
            state = State.LFSPOT;
        }
        return state;
    }

    public void onStart() {
        log("Bot started");
        log("isF is: " + isFishing);
    }

    public void onExit() {
        log("Bot ended!");
    }

    public int randomNum(int i, int k) {
        int number = (int) (Math.random() * (k - i)) + i;
        return number;
    }

    @Override
    public void onPaint(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("String", 15, 266);
        g.drawString("String", 15, 282);
    }
/*
    @Override
    public void onGameMessage(Message message){
        if (message.getMessage().contentEquals("You cast out your net...")){
            isFishing = true;
        }
        if (message.getMessage().contains("You catch")){
            caught++;
        }
    }
    */


}
