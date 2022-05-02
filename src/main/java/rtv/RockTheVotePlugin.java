package rtv;

import java.util.HashSet;

import arc.*;
import arc.util.*;
import mindustry.game.Team;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;

public class RockTheVotePlugin extends Plugin {

    private static double ratio = 0.6;
    private HashSet<String> votes = new HashSet<>();
    private boolean enable = true;

    // register event handlers and create variables in the constructor
    public RockTheVotePlugin() {
        // un-vote on player leave
        Events.on(PlayerLeave.class, e -> {
            Player player = e.player;
            int cur = this.votes.size();
            int req = (int) Math.ceil(ratio * Groups.player.size());
            if(votes.contains(player.uuid())) {
                votes.remove(player.uuid());
                Call.sendMessage("RTV: [accent]" + player.name + "[] 已投票, [green]" + cur + "[] 已经投票, [green]" + req + "[] 通过需求人数");
            }
        });
        // clear votes on game over
        Events.on(GameOverEvent.class, e -> {
            this.votes.clear();
        });
    }


    //register commands that player can invoke in-game
    @Override
    public void registerClientCommands(CommandHandler handler){

        //register a simple reply command
        handler.<Player>register("rtv", "[off]", "通过投票来随机地图", (args, player) -> {
            if (player.admin()){
                this.enable = args.length != 1 || !args[0].equals("off");
            }
            if (!this.enable) {
                player.sendMessage("RTV: 投票随机地图已关闭");
                return;
            }
            this.votes.add(player.uuid());
            int cur = this.votes.size();
            int req = (int) Math.ceil(ratio * Groups.player.size());
            Call.sendMessage("RTV: [accent]" + player.name + "[] 想要换图, [green]" + cur +
                "[] 已经投票, [green]" + req + "[] 投票需求人数");

            if (cur < req) {
                return;
            }

            this.votes.clear();
            Call.sendMessage("RTV: [green] 投票通过，即将换图。");
            Events.fire(new GameOverEvent(Team.crux));
        });
    }
}
