package com.hitsplattracker;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Hitsplat Tracker"
)
public class HitsplatTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		Hitsplat hitsplat = hitsplatApplied.getHitsplat();
		if (!hitsplat.isMine())
		{
			return;
		}

		Actor actor = hitsplatApplied.getActor();
		// Need to delay this a tick so actor.getHealthRatio gets the correct value
		clientThread.invokeLater(() ->
		{
			int ratio = actor.getHealthRatio();
			if (actor instanceof NPC) {
				NPC npc = (NPC) actor;
				log.info("NPC name={}, id={}, index={} type={} amount={} ratio={}",
						npc.getName(), npc.getId(), npc.getIndex(), hitsplat.getHitsplatType(), hitsplat.getAmount(), ratio);
			} else if (actor == client.getLocalPlayer()) {
				log.info("SELF type={} amount={}, ratio={}",
						hitsplat.getHitsplatType(), hitsplat.getAmount(), ratio);
			} else if (actor instanceof Player) {
				log.info("PLAYER name={} type={} amount={}, ratio={}",
						actor.getName(), hitsplat.getHitsplatType(), hitsplat.getAmount(), ratio);
			}
		});
	}
}
