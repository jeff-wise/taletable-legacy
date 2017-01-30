
Calculating a variable's value can sometimes be complicated and may
incur performance penalties. We want to minimize this effort by
caching variable values so that are not recalculated unless necessary.

The solution will probably involve a separate method to tell the
variable it needs to update its value. Then the value() method just
retrieves the cached up-to-date value.
