Transaction Rollback
- To rollback transactions without throwing an Exception:
    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
- Or, to rollback a transaction, throw a RuntimeException
- You can also for e.g. @Transactional(rollbackFor = {Exception.class}) etc.

Repository Transactions
- Spring Repositories are @Transactional.  Look at the source code
  of the methods, for e.g. delete.
- An entity is made managed by doing a find before deleting.  So you
  never get a "Not Managed" exception even when calling delete with
  a detached object.
