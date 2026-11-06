package com.example.data.model
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter=true)
data class WalletPinRequest(val oldPin:String,val newPin:String,val confirmPin:String)
@JsonClass(generateAdapter=true)
data class WalletPinResponse(val success:Boolean,val error:String?=null,val message:String?=null)
@JsonClass(generateAdapter=true)
data class AgentFeeResponse(val success:Boolean,val plan:String?=null,val currency:String?=null,val fee:Double?=null,val error:String?=null)
@JsonClass(generateAdapter=true)
data class AgentLoanWorkflowResponse(val success:Boolean,val operationId:String?=null,val nextStep:String?=null,val client:TargetProfile?=null,val loan:AgentLoanInfo?=null,val fee:Double?=null,val totalDebit:Double?=null,val paid:Double?=null,val commission:Double?=null,val currency:String?=null,val debtRemaining:Double?=null,val reference:String?=null,val error:String?=null)
