package de.tw.energy.controllers

import de.tw.energy.domain.Response
import de.tw.energy.services.AccountService
import de.tw.energy.services.PricePlanService
import java.math.BigDecimal

class PricePlanComparatorController(
    private val pricePlanService: PricePlanService,
    private val accountService: AccountService
) {
    data class CostsPerPlan(
        val pricePlanId: String,
        val pricePlanComparisons: Map<String, BigDecimal>
    )

    fun calculatedCostForEachPricePlan(smartMeterId: String): Response<CostsPerPlan> {
        val pricePlanId = accountService[smartMeterId]
        val consumptionsForPricePlans = pricePlanService.consumptionCostOfElectricityReadingsPerPricePlan(smartMeterId)

        return pricePlanId?.let { pricePlanId ->
            consumptionsForPricePlans?.let { consumptions ->
                Response.body(
                    CostsPerPlan(
                        pricePlanId,
                        consumptions
                    )
                )

            }
        } ?: Response.notFound()
    }

    fun recommendCheapestPricePlans(smartMeterId: String): Response<List<Pair<String, BigDecimal>>> {
        val consumptionsForPricePlans = pricePlanService.consumptionCostOfElectricityReadingsPerPricePlan(smartMeterId)

        return consumptionsForPricePlans?.let { consumptions ->
            val recommendations = consumptions
                .toList()
                .sortedBy { it.second }
            return Response.body(recommendations)
        } ?: Response.notFound()
    }
}
