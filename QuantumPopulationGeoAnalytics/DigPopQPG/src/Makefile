GCC= g++

GCCFLAG= -std=c++0x

# Gdal
GDALDIR = /sw/geosoft/gdal-1.11.2-fgdb
GDALLIB := -L$(GDALDIR)/lib -lgdal

# Target rules
All: RandomField SARasterStat

randomField.o: randomField.c
	$(GCC) -o $@ -c $< 
io.o: io.c
	$(GCC) -I. -I$(GDALDIR)/include -o $@ -c $< 
field.o: field.c
	$(GCC) $(GCCFLAG) -o $@ -c $< 
RandomField: randomField.o io.o field.o
	$(GCC) $(GCCFLAG) -o ../$@ $+ $(GDALLIB)
SARasterStat.o: SARasterStat.c
	$(GCC) -I. -I$(GDALDIR)/include -o $@ -c $< 
SARasterStat: SARasterStat.o
	$(GCC) -o ../$@ $+ $(GDALLIB)
clean:
	rm -f *.o ../RandomField ../SARasterStat
