
 NOAA Forecast - Broadcom
---
##### Dry Run
After cloning the code into this directory:

    ~/stage/broadcom/

A dry run can be performed by sourcing this script:

    cd ~/stage/broadcom/

    . ok

---
##### ßuild
A ßuild can be performed in the directory \

    cd ~/stage/broadcom/

    . bld

  ---
##### Execute
Execute the code with formal, CLI arguments (syntax is ordained by Spring 2.x)
by supplying a set of comma-separated, quoted geocodes in this order  N-Coor, W-Coor:

    cd ~/stage/broadcom/

    mvn spring-boot:run -q -B -Dspring-boot.run.arguments="37.3541,121.9552"

    mvn spring-boot:run -q -B -Dspring-boot.run.arguments="61.17,150"
    mvn spring-boot:run -q -B -Dspring-boot.run.arguments="40.7306,-73.9352"

    mvn spring-boot:run -q -B -Dspring-boot.run.arguments="39.7456,-97.0892"
    mvn spring-boot:run -q -B -Dspring-boot.run.arguments="37.3541,-121.9552"
