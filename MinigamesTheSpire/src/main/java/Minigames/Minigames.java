package Minigames;

import Minigames.util.TextureLoader;
import basemod.*;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.*;
import basemod.patches.whatmod.WhatMod;
import com.badlogic.gdx.Gdx;

import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.curses.AscendersBane;
import com.megacrit.cardcrawl.cards.curses.CurseOfTheBell;
import com.megacrit.cardcrawl.cards.curses.Necronomicurse;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.potions.AbstractPotion;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SpireInitializer
public class Minigames implements
        PostInitializeSubscriber,
        EditStringsSubscriber,
        PostUpdateSubscriber,
        EditKeywordsSubscriber {
    private static SpireConfig modConfig = null;

    public static final Logger logger = LogManager.getLogger(Minigames.class.getName());

    public static void initialize() {
        BaseMod.subscribe(new Minigames());
    }

    private ModPanel settingsPanel;
    private float xPos = 350f, yPos = 750f;
    @Override
    public void receivePostInitialize() {
        //UIStrings UIStrings = CardCrawlGame.languagePack.getUIString(makeID("OptionsMenu"));
        //String[] TEXT = UIStrings.TEXT;
        settingsPanel = new ModPanel();

        BaseMod.registerModBadge(TextureLoader.getTexture(makeImgPath("modBadge.png")), "Minigames The Spire", "erasels", "A mod, boyo.", settingsPanel);
    }

    @Override
    public void receivePostUpdate() {

    }

    @Override
    public void receiveEditStrings() {
        BaseMod.loadCustomStringsFile(UIStrings.class, getModID() + "Resources/loc/"+locPath()+"/uiStrings.json");
    }

    @Override
    public void receiveEditKeywords() {
        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID() + "Resources/loc/"+locPath()+"/keywordStrings.json").readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
            }
        }
    }

    private static String locPath() {
        return "eng";
    }

    public static String makeImgPath(String resourcePath) {
        return getModID() + "Resources/img/" + resourcePath;
    }

    public static String makeUIPath(String resourcePath) {
        return getModID() + "Resources/img/ui/" + resourcePath;
    }

    public static String getModID() {
        return "minigames";
    }

    public static String makeID(String input) {
        return getModID() + ":" + input;
    }

    private void saveConfig() {
        try {
            modConfig.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}