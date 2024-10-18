package org.tbank.fintech.exchange_rates_api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Object containing info about event")
public record Event (
        @Schema(example = "208788")
        Long id,
        @Schema(example = "Выставка «Взгляни на дом свой, ангел»")
        String title,
        @Schema(example = "Проект представляет собой масштабное исследование целого пласта проблем, связанных с образом мегаполисов.")
        String description,
        @Schema(example = "300", description = "The price tag for the event may differ from the real one")
        Long price,
        @Schema(example = "kzn")
        String location,
        @Schema(example = "https://kzn.kudago.com/event/vyistavka-vzglyani-na-dom-svoj-angel/")
        String siteUrl
) {
}
