package com.markdownbookmod.network

import com.markdownbookmod.Markdownbookmod
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar

object ModNetworking {
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar: PayloadRegistrar = event.registrar(Markdownbookmod.ID)
        
        registrar.playToServer(
            UpdateMarkdownBookPayload.TYPE,
            UpdateMarkdownBookPayload.STREAM_CODEC,
            UpdateMarkdownBookPayload::handle
        )
    }
}