package au.id.tmm.estimatesqon.controller

import java.time.LocalDate

import au.id.tmm.estimatesqon.model.Answer

private[controller] class AnswerAssertionInfo(val answerDescription: String,
                                              val findAnswerIn: List[Answer] => Option[Answer],
                                              val qonNumber: String,
                                              val divisionOrAgency: String,
                                              val senator: String,
                                              val topic: String,
                                              val pdfURL: Option[String],
                                              val dateReceived: Option[LocalDate]) {
}
