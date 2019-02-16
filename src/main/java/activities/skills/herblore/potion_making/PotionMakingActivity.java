package activities.skills.herblore.potion_making;

import activities.activity.Activity;
import activities.activity.ActivityType;
import activities.banking.ItemReqBanking;
import util.Executable;
import util.MakeAllInterface;
import util.Sleep;
import util.item_requirement.ItemReq;


public class PotionMakingActivity extends Activity {

    private final Potion potion;
    private Executable bankNode;
    private MakeAllInterface makeAllInterface;

    public PotionMakingActivity(final Potion potion) {
        super(ActivityType.HERBLORE);
        this.potion = potion;
    }

    @Override
    public void onStart() {
        bankNode = new ItemReqBanking(potion.itemReqs);
        bankNode.exchangeContext(getBot());

        makeAllInterface = new MakeAllInterface(1);
        makeAllInterface.exchangeContext(getBot());
    }

    @Override
    public void runActivity() throws InterruptedException {
        if (ItemReq.hasItemRequirements(potion.itemReqs, getInventory())) {
            if (getBank() != null && getBank().isOpen()) {
                getBank().close();
            } else {
                makePotion();
            }
        } else {
            bankNode.run();
            if (bankNode.hasFailed()) {
                setFailed();
            }
        }
    }

    private void makePotion() {
        if (makeAllInterface.isMakeAllScreenOpen()) {
            if (makeAllInterface.makeAll()) {
                Sleep.sleepUntil(() -> !ItemReq.hasItemRequirements(potion.itemReqs, getInventory()) || getDialogues().isPendingContinuation(), 30_000);
            }
        } else if (potion.itemReqs[1].toString().equals(getInventory().getSelectedItemName())) {
            if (getInventory().getItem(potion.itemReqs[0].toString()).interact()) {
                Sleep.sleepUntil(() -> makeAllInterface.isMakeAllScreenOpen(), 3000);
            }
        } else {
            getInventory().getItem(potion.itemReqs[1].toString()).interact("Use");
        }
    }

    @Override
    public PotionMakingActivity copy() {
        return new PotionMakingActivity(potion);
    }
}
