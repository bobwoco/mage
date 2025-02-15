package mage.cards.f;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.BecomesTargetAttachedTriggeredAbility;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.continuous.GainControlTargetEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;
import mage.target.targetpointer.FixedTarget;

/**
 *
 * @author L_J (significantly based on wetterlicht)
 */
public final class FracturedLoyalty extends CardImpl {

    public FracturedLoyalty(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{R}");
        this.subtype.add(SubType.AURA);

        // Enchant creature
        TargetPermanent auraTarget = new TargetCreaturePermanent();
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.Detriment));
        Ability ability = new EnchantAbility(auraTarget);
        this.addAbility(ability);

        // Whenever enchanted creature becomes the target of a spell or ability, that spell or ability's controller gains control of that creature.
        this.addAbility(new BecomesTargetAttachedTriggeredAbility(new FracturedLoyaltyEffect(),
                StaticFilters.FILTER_SPELL_OR_ABILITY_A, SetTargetPointer.PLAYER, false)
                .setTriggerPhrase("Whenever enchanted creature becomes the target of a spell or ability, "));
    }

    private FracturedLoyalty(final FracturedLoyalty card) {
        super(card);
    }

    @Override
    public FracturedLoyalty copy() {
        return new FracturedLoyalty(this);
    }

}

class FracturedLoyaltyEffect extends OneShotEffect {

    FracturedLoyaltyEffect() {
        super(Outcome.GainControl);
        this.staticText = "that spell or ability's controller gains control of that creature";
    }

    private FracturedLoyaltyEffect(final FracturedLoyaltyEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        // In the case that Fractured Loyalty is blinked
        Permanent enchantment = (Permanent) game.getLastKnownInformation(source.getSourceId(), Zone.BATTLEFIELD);
        if (enchantment == null) {
            // It was not blinked, use the standard method
            enchantment = game.getPermanentOrLKIBattlefield(source.getSourceId());
        }
        if (enchantment == null || enchantment.getAttachedTo() == null) {
            return false;
        }
        Permanent enchantedCreature = game.getPermanent(enchantment.getAttachedTo());
        if (enchantedCreature == null) {
            return false;
        }
        Player controller = game.getPlayer(enchantedCreature.getControllerId());
        if (controller != null && !enchantedCreature.isControlledBy(this.getTargetPointer().getFirst(game, source))) {
            ContinuousEffect effect = new GainControlTargetEffect(Duration.EndOfGame, this.getTargetPointer().getFirst(game, source));
            effect.setTargetPointer(new FixedTarget(enchantment.getAttachedTo(), game));
            game.addEffect(effect, source);
            return true;
        }
        return false;
    }

    @Override
    public FracturedLoyaltyEffect copy() {
        return new FracturedLoyaltyEffect(this);
    }

}
