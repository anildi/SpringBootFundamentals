package ttl.larku.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ttl.larku.service.ServiceToTestTransactions;

@RestController
public class ControllerToTestTransactions {

   @Autowired
   private ServiceToTestTransactions service;

   @GetMapping("/allgood")
   public ResponseEntity<?> doTransaction() {
      return ResponseEntity.ok(service.addStudentGood());
   }

   @GetMapping("/appException")
   public ResponseEntity<?> checkedException() throws Exception {
      return ResponseEntity.ok(service.addStudentApplicationException());
   }

   @GetMapping("/rollBackFor")
   public ResponseEntity<?> rollBackFor() throws Exception {
      return ResponseEntity.ok(service.addStudentRollBackForSpecifiedApplicationExceptions());
   }

   @GetMapping("/runtimeException")
   public ResponseEntity<?> runtimeException() {
      return ResponseEntity.ok(service.addStudentRuntimeException());
   }

   @GetMapping("/ourOwnTrans")
   public ResponseEntity<?> ourOwnTransaction() throws ServiceToTestTransactions.MyApplicationExceptionIWantToRollBackFor {
      return ResponseEntity.ok(service.addStudentWithOurOwnTransaction());
   }

   @GetMapping("/theirEM")
   public ResponseEntity<?> theirEntityManager() throws ServiceToTestTransactions.MyApplicationExceptionIWantToRollBackFor {
      return ResponseEntity.ok(service.addStudentWithContainerEntityManager());
   }

}
