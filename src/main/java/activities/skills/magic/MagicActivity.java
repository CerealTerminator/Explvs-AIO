package activities.skills.magic;

import activities.activity.Activity;

public class MagicActivity extends Activity {

    private final Spell spell;

    public MagicActivity(final Spell spell) {
        super(null);//ActivityType.MAGIC);
        this.spell = spell;
    }

    @Override
    public void runActivity() throws InterruptedException {
    }

    @Override
    public MagicActivity copy() {
        return new MagicActivity(spell);
    }
}
