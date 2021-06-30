/*
 * MegaMek - Copyright (C) 2003,2004,2005 Ben Mazur (bmazur@sev.org)
 * Copyright (c) 2021 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMek.
 *
 * MegaMek is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MegaMek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MegaMek. If not, see <http://www.gnu.org/licenses/>.
 */
package megamek.client.ui.swing;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import megamek.MegaMek;
import megamek.client.Client;
import megamek.client.ui.swing.util.UIUtil;

import static megamek.client.ui.Messages.*;
import megamek.common.*;
import megamek.common.Entity.WeaponSortOrder;
import megamek.common.IGame.Phase;
import megamek.common.preference.IPreferenceChangeListener;
import megamek.common.preference.PreferenceChangeEvent;
import static megamek.client.ui.swing.ClientGUI.*;
import static javax.swing.KeyStroke.*;
import static java.awt.event.KeyEvent.*;


/**
 * The menu bar that is used across MM, i.e. in the main menu, the board editor and
 * the lobby and game. 
 */
public class CommonMenuBar extends JMenuBar implements ActionListener, IPreferenceChangeListener {

    private static final GUIPreferences GUIP = GUIPreferences.getInstance();
    
    /** True when this menu is attached to the board editor. */
    private boolean isBoardEditor = false;
    /** True when this menu is attached to the game's main menu. */
    private boolean isMainMenu = false;
    /** True when this menu is attached to a client (lobby or ingame). */
    private boolean isGame = false;
    /** The current phase of the game, if any. */
    private IGame.Phase phase = IGame.Phase.PHASE_UNKNOWN;
    private Entity entity;

    // The Game menu
    private JMenuItem gameSave = new JMenuItem(getString("CommonMenuBar.fileGameSave"));
    private JMenuItem gameQSave = new JMenuItem(getString("CommonMenuBar.fileGameQuickSave")); 
    private JMenuItem gameQLoad = new JMenuItem(getString("CommonMenuBar.fileGameQuickLoad"));
    private JMenuItem gameSaveServer = new JMenuItem(getString("CommonMenuBar.fileGameSaveServer"));
    private JMenuItem gameRoundReport = new JMenuItem(getString("CommonMenuBar.viewRoundReport"));
    private JMenuItem gameReplacePlayer = new JMenuItem(getString("CommonMenuBar.replacePlayer"));
    private JMenuItem gamePlayerList = new JMenuItem(getString("CommonMenuBar.viewPlayerList"));
    private JMenuItem gameGameOptions = new JMenuItem(getString("CommonMenuBar.viewGameOptions"));
    private JMenuItem gamePlayerSettings = new JMenuItem(getString("CommonMenuBar.viewPlayerSettings"));
    
    // The Units menu
    private JMenuItem fileUnitsReinforce = new JMenuItem(getString("CommonMenuBar.fileUnitsReinforce"));
    private JMenuItem fileUnitsReinforceRAT = new JMenuItem(getString("CommonMenuBar.fileUnitsReinforceRAT"));
    private JMenuItem fileRefreshCache = new JMenuItem(getString("CommonMenuBar.fileUnitsRefreshUnitCache"));
    private JMenuItem fileUnitsPaste = new JMenuItem(getString("CommonMenuBar.fileUnitsPaste"));

    // The Board menu
    private JMenuItem boardNew = new JMenuItem(getString("CommonMenuBar.fileBoardNew"));
    private JMenuItem boardOpen = new JMenuItem(getString("CommonMenuBar.fileBoardOpen"));
    private JMenuItem boardSave = new JMenuItem(getString("CommonMenuBar.fileBoardSave"));
    private JMenuItem boardSaveAs = new JMenuItem(getString("CommonMenuBar.fileBoardSaveAs"));
    private JMenuItem boardSaveAsImage = new JMenuItem(getString("CommonMenuBar.fileBoardSaveAsImage"));
    private JMenuItem boardSaveAsImageUnits = new JMenuItem(getString("CommonMenuBar.fileBoardSaveAsImageUnits"));
    private JMenuItem boardUndo = new JMenuItem(getString("CommonMenuBar.boardUndo"));
    private JMenuItem boardRedo = new JMenuItem(getString("CommonMenuBar.boardRedo"));
    private JMenuItem boardChangeTheme = new JMenuItem(getString("CommonMenuBar.viewChangeTheme"));
    private JMenuItem boardRaise = new JMenuItem(getString("CommonMenuBar.boardRaise"));
    private JMenuItem boardClear = new JMenuItem(getString("CommonMenuBar.boardClear"));
    private JMenuItem boardFlatten = new JMenuItem(getString("CommonMenuBar.boardFlatten"));
    private JMenuItem boardFlood = new JMenuItem(getString("CommonMenuBar.boardFlood"));
    private JMenu boardRemove = new JMenu(getString("CommonMenuBar.boardRemove"));
    private JMenuItem boardRemoveForests = new JMenuItem(getString("CommonMenuBar.boardRemoveForests"));
    private JMenuItem boardRemoveWater = new JMenuItem(getString("CommonMenuBar.boardRemoveWater"));
    private JMenuItem boardRemoveRoads = new JMenuItem(getString("CommonMenuBar.boardRemoveRoads"));
    private JMenuItem boardRemoveBuildings = new JMenuItem(getString("CommonMenuBar.boardRemoveBuildings"));

    // The View menu
    private JCheckBoxMenuItem viewMiniMap = new JCheckBoxMenuItem(getString("CommonMenuBar.viewMiniMap"));
    private JMenuItem viewMekDisplay = new JMenuItem(getString("CommonMenuBar.viewMekDisplay"));
    private JMenuItem viewAccessibilityWindow = new JMenuItem(getString("CommonMenuBar.viewAccessibilityWindow"));
    private JCheckBoxMenuItem viewKeybindsOverlay = new JCheckBoxMenuItem(getString("CommonMenuBar.viewKeyboardShortcuts"));
    private JMenuItem viewZoomIn = new JMenuItem(getString("CommonMenuBar.viewZoomIn"));
    private JMenuItem viewZoomOut = new JMenuItem(getString("CommonMenuBar.viewZoomOut"));
    private JMenuItem viewResetWindowPositions = new JMenuItem(getString("CommonMenuBar.viewResetWindowPos"));
    private JCheckBoxMenuItem toggleIsometric = new JCheckBoxMenuItem(getString("CommonMenuBar.viewToggleIsometric"));
    private JCheckBoxMenuItem toggleFieldOfFire = new JCheckBoxMenuItem(getString("CommonMenuBar.viewToggleFieldOfFire"));
    private JCheckBoxMenuItem toggleFovHighlight = new JCheckBoxMenuItem(getString("CommonMenuBar.viewToggleFovHighlight"));
    private JCheckBoxMenuItem toggleFovDarken = new JCheckBoxMenuItem(getString("CommonMenuBar.viewToggleFovDarken"));
    private JCheckBoxMenuItem toggleFiringSolutions = new JCheckBoxMenuItem(getString("CommonMenuBar.viewToggleFiringSolutions"));
    private JCheckBoxMenuItem viewMovementEnvelope = new JCheckBoxMenuItem(getString("CommonMenuBar.movementEnvelope"));
    private JMenuItem viewMovModEnvelope = new JMenuItem(getString("CommonMenuBar.movementModEnvelope"));
    private JMenuItem viewLOSSetting = new JMenuItem(getString("CommonMenuBar.viewLOSSetting"));
    private JCheckBoxMenuItem viewUnitOverview = new JCheckBoxMenuItem(getString("CommonMenuBar.viewUnitOverview"));
    private JMenuItem viewClientSettings = new JMenuItem(getString("CommonMenuBar.viewClientSettings"));
    private JMenuItem viewIncGUIScale = new JMenuItem(getString("CommonMenuBar.viewIncGUIScale"));
    private JMenuItem viewDecGUIScale = new JMenuItem(getString("CommonMenuBar.viewDecGUIScale"));
    
    // The Help menu
    private JMenuItem helpContents = new JMenuItem(getString("CommonMenuBar.helpContents"));
    private JMenuItem helpSkinning = new JMenuItem(getString("CommonMenuBar.helpSkinning"));
    private JMenuItem helpAbout = new JMenuItem(getString("CommonMenuBar.helpAbout"));
    
    // The Deploy menu
    private JMenuItem deployMinesConventional;
    private JMenuItem deployMinesCommand;
    private JMenuItem deployMinesVibrabomb;
    private JMenuItem deployMinesActive;
    private JMenuItem deployMinesInferno;
    
    // The Firing Action menu
    private JMenuItem fireSaveWeaponOrder = new JMenuItem(getString("CommonMenuBar.fireSaveWeaponOrder"));
    
    /** Contains all ActionListeners that have registered themselves with this menu bar. */
    private final List<ActionListener> actionListeners = new ArrayList<>();
    
    /** Maps the Action Command to the respective MenuItem. */
    private final Map<String, JMenuItem> itemMap = new HashMap<>();

    /** Creates a MegaMek menu bar for the given client (for the lobby or ingame). */
    public CommonMenuBar(Client parent) {
        this();
        isGame = true;
        updateEnabledStates();
    }
    
    /** Creates a MegaMek menu bar for the board editor. */
    public CommonMenuBar(BoardEditor be) {
        this();
        isBoardEditor = true;
        updateEnabledStates();
    }
    
    /** Creates a MegaMek menu bar for the main menu. */
    public CommonMenuBar(MegaMekGUI mmg) {
        this();
        isMainMenu = true;
        updateEnabledStates();
    }

    /** Creates the common MegaMek menu bar. */
    public CommonMenuBar() {
        // Create the Game menu
        JMenu menu = new JMenu(getString("CommonMenuBar.GameMenu"));
        menu.setMnemonic(KeyEvent.VK_G);
        add(menu);
        initMenuItem(gameSave, menu, FILE_GAME_SAVE);
        initMenuItem(gameQSave, menu, FILE_GAME_QSAVE, getKeyStroke(VK_S, CTRL_DOWN_MASK));
        initMenuItem(gameQLoad, menu, FILE_GAME_QLOAD, getKeyStroke(VK_L, CTRL_DOWN_MASK));
        initMenuItem(gameSaveServer, menu, FILE_GAME_SAVE_SERVER);
        menu.addSeparator();
        initMenuItem(gameRoundReport, menu, VIEW_ROUND_REPORT, getKeyStroke(VK_R, CTRL_DOWN_MASK));
        menu.addSeparator();
        initMenuItem(gameReplacePlayer, menu, FILE_GAME_REPLACE_PLAYER);
        initMenuItem(gamePlayerList, menu, VIEW_PLAYER_LIST);
        menu.addSeparator();
        initMenuItem(gameGameOptions, menu, VIEW_GAME_OPTIONS);
        initMenuItem(gamePlayerSettings, menu, VIEW_PLAYER_SETTINGS);

        // Create the Unit List sub-menu.
        menu = new JMenu(getString("CommonMenuBar.UnitListMenu")); 
        menu.setMnemonic(KeyEvent.VK_U);
        add(menu);
        initMenuItem(fileUnitsPaste, menu, FILE_UNITS_PASTE, getKeyStroke(VK_V, CTRL_DOWN_MASK));
        initMenuItem(fileUnitsReinforce, menu, FILE_UNITS_REINFORCE);
        initMenuItem(fileUnitsReinforceRAT, menu, FILE_UNITS_REINFORCE_RAT);
        menu.addSeparator();
        initMenuItem(fileRefreshCache, menu, FILE_REFRESH_CACHE);

        // Create the Board sub-menu.
        menu = new JMenu(getString("CommonMenuBar.BoardMenu")); 
        menu.setMnemonic(KeyEvent.VK_B);
        add(menu);
        initMenuItem(boardNew, menu, BOARD_NEW);
        initMenuItem(boardOpen, menu, BOARD_OPEN);
        initMenuItem(boardSave, menu, BOARD_SAVE);
        initMenuItem(boardSaveAs, menu, BOARD_SAVE_AS);
        menu.addSeparator();
        initMenuItem(boardSaveAsImage, menu, BOARD_SAVE_AS_IMAGE);
        boardSaveAsImage.setToolTipText(getString("CommonMenuBar.fileBoardSaveAsImage.tooltip"));
        initMenuItem(boardSaveAsImageUnits, menu, BOARD_SAVE_AS_IMAGE_UNITS);
        boardSaveAsImageUnits.setToolTipText(getString("CommonMenuBar.fileBoardSaveAsImageUnits.tooltip"));
        menu.addSeparator();
        initMenuItem(boardUndo, menu, BOARD_UNDO, getKeyStroke(VK_Z, CTRL_DOWN_MASK));
        initMenuItem(boardRedo, menu, BOARD_REDO);
        menu.addSeparator();
        initMenuItem(boardChangeTheme, menu, VIEW_CHANGE_THEME);
        initMenuItem(boardRaise, menu, BOARD_RAISE);
        initMenuItem(boardClear, menu, BOARD_CLEAR);
        initMenuItem(boardFlatten, menu, BOARD_FLATTEN);
        initMenuItem(boardFlood, menu, BOARD_FLOOD);
        boardRemove = new JMenu(getString("CommonMenuBar.boardRemove"));
        menu.add(boardRemove);
        initMenuItem(boardRemoveForests, boardRemove, BOARD_REMOVE_FORESTS);
        initMenuItem(boardRemoveWater, boardRemove, BOARD_REMOVE_WATER);
        initMenuItem(boardRemoveRoads, boardRemove, BOARD_REMOVE_ROADS);
        initMenuItem(boardRemoveBuildings, boardRemove, BOARD_REMOVE_BUILDINGS);
        
        // Create the view menu.
        menu = new JMenu(getString("CommonMenuBar.ViewMenu"));
        menu.setMnemonic(KeyEvent.VK_V);
        add(menu);
        initMenuItem(viewClientSettings, menu, VIEW_CLIENT_SETTINGS, getKeyStroke(VK_C, ALT_DOWN_MASK));
        initMenuItem(viewIncGUIScale, menu, VIEW_INCGUISCALE, getKeyStroke(VK_ADD, CTRL_DOWN_MASK));
        initMenuItem(viewDecGUIScale, menu, VIEW_DECGUISCALE, getKeyStroke(VK_SUBTRACT, CTRL_DOWN_MASK));
        menu.addSeparator();
        initMenuItem(viewResetWindowPositions, menu, VIEW_RESET_WINDOW_POSITIONS);
        initMenuItem(viewAccessibilityWindow, menu, VIEW_ACCESSIBILITY_WINDOW);
        viewAccessibilityWindow.setMnemonic(KeyEvent.VK_A);
        menu.addSeparator();
        initMenuItem(viewKeybindsOverlay, menu, VIEW_KEYBINDS_OVERLAY, getKeyStroke(VK_K, CTRL_DOWN_MASK));
        viewKeybindsOverlay.setState(GUIP.getBoolean(GUIPreferences.SHOW_KEYBINDS_OVERLAY));
        initMenuItem(viewUnitOverview, menu, VIEW_UNIT_OVERVIEW, getKeyStroke(VK_U, CTRL_DOWN_MASK));
        viewUnitOverview.setState(GUIP.getShowUnitOverview());
        initMenuItem(viewZoomIn, menu, VIEW_ZOOM_IN);
        initMenuItem(viewZoomOut, menu, VIEW_ZOOM_OUT);
        initMenuItem(toggleIsometric, menu, VIEW_TOGGLE_ISOMETRIC, getKeyStroke(VK_T, ALT_DOWN_MASK));
        toggleIsometric.setState(GUIP.getIsometricEnabled()); 
        menu.addSeparator();
        initMenuItem(viewMekDisplay, menu, VIEW_MEK_DISPLAY, getKeyStroke(VK_D, CTRL_DOWN_MASK));
        initMenuItem(viewMiniMap, menu, VIEW_MINI_MAP, getKeyStroke(VK_M, CTRL_DOWN_MASK));
        viewMiniMap.setState(GUIP.getMinimapEnabled());
        menu.addSeparator();
        initMenuItem(toggleFovDarken, menu, VIEW_TOGGLE_FOV_DARKEN);
        initMenuItem(viewLOSSetting, menu, VIEW_LOS_SETTING, getKeyStroke(VK_L, CTRL_DOWN_MASK));
        initMenuItem(toggleFovHighlight, menu, VIEW_TOGGLE_FOV_HIGHLIGHT);
        initMenuItem(toggleFieldOfFire, menu, VIEW_TOGGLE_FIELD_OF_FIRE);
        initMenuItem(toggleFiringSolutions, menu, VIEW_TOGGLE_FIRING_SOLUTIONS);
        initMenuItem(viewMovementEnvelope, menu, VIEW_MOVE_ENV, getKeyStroke(VK_Q, CTRL_DOWN_MASK));
        initMenuItem(viewMovModEnvelope, menu, VIEW_MOVE_MOD_ENV, getKeyStroke(VK_W, CTRL_DOWN_MASK));
        toggleFovDarken.setState(GUIP.getFovDarken()); 
        toggleFovDarken.setToolTipText(getString("CommonMenuBar.viewToggleFovDarkenTooltip"));
        toggleFovHighlight.setState(GUIP.getFovHighlight());
        toggleFieldOfFire.setState(GUIP.getShowFieldOfFire());
        toggleFieldOfFire.setToolTipText(getString("CommonMenuBar.viewToggleFieldOfFireToolTip"));
        toggleFiringSolutions.setToolTipText(getString("CommonMenuBar.viewToggleFiringSolutionsToolTip")); 
        toggleFiringSolutions.setState(GUIP.getFiringSolutions());
        viewMovementEnvelope.setState(GUIP.getMoveEnvelope());
        menu.addSeparator();
        initMenuItem(fireSaveWeaponOrder, menu, FIRE_SAVE_WEAPON_ORDER);
        /* TODO: moveTraitor = createMenuItem(menu, getString("CommonMenuBar.moveTraitor"), MovementDisplay.MOVE_TRAITOR);  */

        // Create the Help menu
        menu = new JMenu(getString("CommonMenuBar.HelpMenu")); 
        menu.setMnemonic(KeyEvent.VK_H);
        add(menu);
        initMenuItem(helpContents, menu, HELP_CONTENTS);
        initMenuItem(helpSkinning, menu, HELP_SKINNING);
        menu.addSeparator();
        initMenuItem(helpAbout, menu, HELP_ABOUT);

        adaptToGUIScale();
        GUIP.addPreferenceChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        
        // Changes that are independent of the current state of MM
        if (event.getActionCommand().equals(ClientGUI.VIEW_INCGUISCALE)) {
            float guiScale = GUIP.getGUIScale();
            if (guiScale < ClientGUI.MAX_GUISCALE) {
                GUIP.setValue(GUIPreferences.GUI_SCALE, guiScale + 0.1);
            }
        } else if (event.getActionCommand().equals(ClientGUI.VIEW_DECGUISCALE)) {
            float guiScale = GUIP.getGUIScale();
            if (guiScale > ClientGUI.MIN_GUISCALE) {
                GUIP.setValue(GUIPreferences.GUI_SCALE, guiScale - 0.1);
            }
        } else if (event.getActionCommand().equals(ClientGUI.VIEW_MINI_MAP)) {
            // Here the actual user preference is changed. The listeners should
            // just adjust the visibility of the map dialog based on guip.getMinimapEnabled()
            GUIP.setMinimapEnabled(!GUIP.getMinimapEnabled());
        }
        
        // Pass the action on to each of our listeners.
        actionListeners.forEach(l -> l.actionPerformed(event));
    }

    /**
     * Register an object that wishes to be alerted when an item on this menu
     * bar has been selected. <p/> Please note, the ActionCommand property of
     * the action event will inform the listener as to which menu item that has
     * been selected. Not all listeners will be interested in all menu items.
     *
     * @param listener - the <code>ActionListener</code> that wants to
     *            register itself.
     */
    public synchronized void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Remove an object that was being alerted when an item on this menu bar was
     * selected.
     *
     * @param listener - the <code>ActionListener</code> that wants to be
     *            removed.
     */
    public synchronized void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    /** Manages the enabled states of the menu items depending on where the menu is empoyed. */
    private synchronized void updateEnabledStates() {
        boolean isLobby = isGame && (phase == Phase.PHASE_LOUNGE);
        boolean isInGame = isGame && phase.isDuringOrAfter(Phase.PHASE_DEPLOYMENT);
        boolean isInGameBoardView = isInGame && phase.isOnMap();
        boolean isBoardView = isInGameBoardView || isBoardEditor;
        boolean canSave = (phase != Phase.PHASE_UNKNOWN) && (phase != Phase.PHASE_SELECTION)
                && (phase != Phase.PHASE_EXCHANGE) && (phase != Phase.PHASE_VICTORY)
                && (phase != Phase.PHASE_STARTING_SCENARIO);
        
        viewAccessibilityWindow.setEnabled(false);
        
        boardChangeTheme.setEnabled(isBoardEditor);
        boardUndo.setEnabled(isBoardEditor);
        boardRedo.setEnabled(isBoardEditor);
        boardRaise.setEnabled(isBoardEditor);
        boardClear.setEnabled(isBoardEditor);
        boardFlood.setEnabled(isBoardEditor);
        boardRemove.setEnabled(isBoardEditor);
        boardRemoveBuildings.setEnabled(isBoardEditor);
        boardRemoveWater.setEnabled(isBoardEditor);
        boardRemoveRoads.setEnabled(isBoardEditor);
        boardRemoveForests.setEnabled(isBoardEditor);
        boardFlatten.setEnabled(isBoardEditor);
        gameQLoad.setEnabled(isMainMenu);
        
        gameSave.setEnabled(isLobby || (isInGame && canSave));
        gameSaveServer.setEnabled(isLobby || (isInGame && canSave));
        gameQSave.setEnabled(isLobby || (isInGame && canSave));
        gameReplacePlayer.setEnabled(isLobby || (isInGame && canSave));
        boardSave.setEnabled(isBoardEditor);
        boardSaveAs.setEnabled(isBoardEditor || isInGame); // TODO: should work in the lobby
        boardSaveAsImage.setEnabled(isBoardEditor || isInGame); // TODO: should work in the lobby
        boardNew.setEnabled(isBoardEditor || isMainMenu);
        boardOpen.setEnabled(isBoardEditor || isMainMenu);
        fileUnitsPaste.setEnabled(isLobby);
        fileUnitsReinforce.setEnabled(isLobby || isInGame);
        fileUnitsReinforceRAT.setEnabled(isLobby || isInGame);
        boardSaveAsImageUnits.setEnabled(isInGame);
        gamePlayerList.setEnabled(isInGame);
        
        gameGameOptions.setEnabled(isInGame || isLobby);
        gamePlayerSettings.setEnabled(isInGame);
        
        viewMiniMap.setEnabled(isBoardView);
        viewZoomIn.setEnabled(isBoardView);
        viewZoomOut.setEnabled(isBoardView);
        toggleIsometric.setEnabled(isBoardView);
        viewKeybindsOverlay.setEnabled(isBoardView);
        
        viewLOSSetting.setEnabled(isInGameBoardView);
        viewUnitOverview.setEnabled(isInGameBoardView);
        toggleFieldOfFire.setEnabled(isInGameBoardView);
        toggleFovHighlight.setEnabled(isInGameBoardView);
        toggleFovDarken.setEnabled(isInGameBoardView);
        toggleFiringSolutions.setEnabled(isInGameBoardView);
        viewMovementEnvelope.setEnabled(isInGameBoardView);
        viewMovModEnvelope.setEnabled(isInGameBoardView);
        gameRoundReport.setEnabled(isInGameBoardView);
        viewMekDisplay.setEnabled(isInGameBoardView);
            
        updateSaveWeaponOrderMenuItem();
    }

    public synchronized void updateSaveWeaponOrderMenuItem() {
        if (entity != null) {
            WeaponOrderHandler.WeaponOrder storedWeapOrder = 
                    WeaponOrderHandler.getWeaponOrder(entity.getChassis(), entity.getModel());
            WeaponOrderHandler.WeaponOrder newWeapOrder = new WeaponOrderHandler.WeaponOrder();
            newWeapOrder.orderType = entity.getWeaponSortOrder();
            newWeapOrder.customWeaponOrderMap = entity.getCustomWeaponOrder();
            boolean isDefault = (storedWeapOrder == null)
                    && (newWeapOrder.orderType == WeaponSortOrder.DEFAULT);
            fireSaveWeaponOrder.setEnabled(!newWeapOrder.equals(storedWeapOrder) && !isDefault);
        } else {
            fireSaveWeaponOrder.setEnabled(false);
        }
    }

    /**
     * Identify to the menu bar which phase is currently in progress
     *
     * @param current - the <code>int</code> value of the current phase (the
     *            valid values for this argument are defined as constants in the
     *            <code>Game</code> class).
     */
    public synchronized void setPhase(IGame.Phase current) {
        entity = null;
        phase = current;
        // There are certain phases where we shouldn't allow the board to be
        //  saved, however the vast majority of phases should allow it
        switch (current) {
            case PHASE_STARTING_SCENARIO:
            case PHASE_UNKNOWN:
            case PHASE_LOUNGE:
            case PHASE_SELECTION:
            case PHASE_EXCHANGE:
//                setBoard(false);
                break;
            default:
//                setBoard(true);
                break;
        }
        updateEnabledStates();
    }
    
    public synchronized void setEnabled(String command, boolean enabled) {
        if (itemMap.containsKey(command)) {
            itemMap.get(command).setEnabled(enabled);
        } else {
            MegaMek.getLogger().error("ActionCommand " + command + " not recognized.");
            return;
        }
    }

    public synchronized void setDeployConventionalEnabled(int nbr) {
        deployMinesConventional.setText(getString("CommonMenuBar.Minefield", nbr)); 
        deployMinesConventional.setEnabled(nbr > 0);
    }

    public synchronized void setDeployCommandEnabled(int nbr) {
        deployMinesCommand.setText(getString("CommonMenuBar.Command", nbr)); 
        // Cannot ever deploy command mines...
        deployMinesCommand.setEnabled(false);
    }

    public synchronized void setDeployVibrabombEnabled(int nbr) {
        deployMinesVibrabomb.setText(getString("CommonMenuBar.Vibrabomb", nbr)); 
        deployMinesVibrabomb.setEnabled(nbr > 0);
    }

    public synchronized void setDeployActiveEnabled(int nbr) {
        deployMinesActive.setText(getString("CommonMenuBar.Active", nbr)); 
        deployMinesActive.setEnabled(nbr > 0);
    }

    public synchronized void setDeployInfernoEnabled(int nbr) {
        deployMinesInferno.setText(getString("CommonMenuBar.Inferno", nbr)); 
        deployMinesInferno.setEnabled(nbr > 0);
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent e) {
        if (e.getName().equals(GUIPreferences.USE_ISOMETRIC)) {
            toggleIsometric.setSelected((Boolean)e.getNewValue());
        } else if (e.getName().equals(GUIPreferences.SHOW_FIELD_OF_FIRE)) {
            toggleFieldOfFire.setSelected((Boolean)e.getNewValue());
        } else if (e.getName().equals(GUIPreferences.SHOW_KEYBINDS_OVERLAY)) {
            viewKeybindsOverlay.setSelected((Boolean)e.getNewValue());
        } else if (e.getName().equals(GUIPreferences.SHOW_UNIT_OVERVIEW)) {
            viewUnitOverview.setSelected((Boolean)e.getNewValue());
        } else if (e.getName().equals(GUIPreferences.GUI_SCALE)) {
            adaptToGUIScale();
        } else if (e.getName().equals(GUIPreferences.MINIMAP_ENABLED)) {
            viewMiniMap.setSelected(GUIP.getMinimapEnabled());
        } else if (e.getName().equals(GUIPreferences.SHOW_KEYBINDS_OVERLAY)) {
            viewKeybindsOverlay.setSelected(GUIP.getBoolean(GUIPreferences.SHOW_KEYBINDS_OVERLAY));
        }
    }
    
    /** Adapts the menu (the font size) to the current GUI scale. */
    private void adaptToGUIScale() {
        UIUtil.scaleMenu(this);
    }

    /** Removes this as listener. */
    public void die() {
        GUIP.removePreferenceChangeListener(this);        
    }
    
    private void initMenuItem(JMenuItem item, JMenu menu, String command, KeyStroke accel) {
        item.setAccelerator(accel);
        initMenuItem(item, menu, command);
    }
    
    private void initMenuItem(JMenuItem item, JMenu menu, String command) {
        item.addActionListener(this);
        item.setActionCommand(command);
        itemMap.put(command, item);
        menu.add(item);
    }
    
    
}
