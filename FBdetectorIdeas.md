### TODO ###

  * Create / use UI API database
  * Warning about using locks (synchronized methods/blocks) from UI code

### Fix existing code ###

  * Remove recursion for call graph
  * Use 5 flags instead of 3 (UI/NUI/possible UI/possible NUI/unknown)
  * Check if one can re-use existing call trees
  * Check if one can re-use existing inheritance graphs
  * Criteria for UI/non UI are sometimes weak or wrong.
    * Example: a synchronized method is not necessarily non-UI, it can be just a bug.

### Current state of detector ###

  * Hardcoded UI/non UI class/package names in DetectoCheckClassPattern
  * asyncExec() syncExec() are considered to be invoked from non UI threads
  * evaluation of statistical information uses too simple logic (count of UI methods bigger then non UI etc)