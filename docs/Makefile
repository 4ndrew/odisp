# Makefile for all documents
#
# $Id: Makefile,v 1.8 2003/10/21 20:17:18 dron Exp $

# folders & files
TEXDOCS=impl.tex \
		  gui_imp.tex \
        tz.tex
TEXDVIS=$(TEXDOCS:.tex=.dvi)
XMLMSGROOT=messages.xml
TOOLS=../tools

# tools
TEXPROC=latex
FIND=find
RM=rm -rf
XSLTPROC=xsltproc

all: texdocs xmldocs 

texdocs: $(TEXDVIS)

xmldocs: $(XMLMSGROOT:.xml=.html)

$(XMLMSGROOT:.xml=.html): $(XMLMSGROOT)
	@echo Generating HTML reference for messages
	$(XSLTPROC) --xinclude --output messages.html $(TOOLS)/messages.xsl messages.xml
	$(RM) $(XMLMSGROOT)

$(XMLMSGROOT)!
	@echo Searching source tree for used messages
	$(RM) $(XMLMSGROOT)
	$(FIND) .. -name \*java -exec grep getNewMessage\(\" \{\} \; | sed -e 's|.*getNewMessage||' -e 's|,.*||' | awk -F\" -f $(TOOLS)/make_messages.awk > $(XMLMSGROOT)


$(TEXDOCS:.tex=.dvi): $(@:.dvi=.tex)
	@echo Generating TeX docs...
	$(TEXPROC) $>
	$(TEXPROC) $>

clean:
	@echo Cleaning...
	$(RM) $(TEXDOCS:.tex=.log) $(TEXDOCS:.tex=.aux) $(TEXDOCS:.tex=.toc) $(XMLMSGROOT)

distclean: clean
	@echo Full clean...
	$(RM) $(TEXDOCS:.tex=.dvi) $(TEXDOCS:.tex=.toc)
