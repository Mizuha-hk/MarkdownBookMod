# MarkdownBook Data Persistence Implementation

## Problem Analysis
The MarkdownBook mod was experiencing data loss when worlds were reloaded. The issue was that book content edited through the GUI was only being stored locally on the client side, not synchronized to the server.

## Solution Overview
Implemented a client-server synchronization system using NeoForge's network packet system to ensure that when a player saves book content, it's properly synchronized to the server for persistence.

## Implementation Details

### 1. Network Packet System
- **UpdateMarkdownBookPayload**: A custom packet that carries title, content, and hand information from client to server
- **ModNetworking**: Handles packet registration and routing
- Registered in the main mod class using RegisterPayloadHandlersEvent

### 2. Client-Side Changes
- **MarkdownBookScreen**: Modified to send network packet instead of just modifying local ItemStack
- **MarkdownBook**: Updated to pass InteractionHand parameter to the screen

### 3. Server-Side Handling
- Packet handler receives data and updates the ItemStack in the player's inventory
- Calls `broadcastChanges()` to ensure the inventory change is saved to disk

## Files Modified
1. `src/main/kotlin/com/markdownbookmod/Markdownbookmod.kt` - Added network registration
2. `src/main/kotlin/com/markdownbookmod/item/MarkdownBook.kt` - Pass hand parameter to screen
3. `src/main/kotlin/com/markdownbookmod/screen/MarkdownBookScreen.kt` - Send network packet on save
4. `src/main/kotlin/com/markdownbookmod/network/ModNetworking.kt` - Network registration (new)
5. `src/main/kotlin/com/markdownbookmod/network/UpdateMarkdownBookPayload.kt` - Packet implementation (new)

## How It Works
1. Player opens MarkdownBook in either main or off hand
2. Player edits title and content in the GUI
3. When saving, instead of just modifying the local ItemStack, a network packet is sent to the server
4. Server receives the packet and updates the ItemStack in the player's inventory
5. Server marks the inventory as dirty, ensuring changes are saved to disk
6. Data persists across world reloads

This implementation follows standard Minecraft modding practices for client-server synchronization and ensures data persistence across all game scenarios.