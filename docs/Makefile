# Makefile for all documents
#
# $Id: Makefile,v 1.6 2003/10/04 12:57:57 valeks Exp $

TEXDOCS=impl.tex \
        tz.tex

TEXDVIS=$(TEXDOCS:.tex=.dvi)

TEXPROC=latex

RM=rm -rf

all: $(TEXDVIS)

$(TEXDOCS:.tex=.dvi): $(@:.dvi=.tex)
	@echo Generating TeX docs...
	$(TEXPROC) $>
	$(TEXPROC) $>

clean:
	@echo Cleaning...
	$(RM) $(TEXDOCS:.tex=.log) $(TEXDOCS:.tex=.aux) $(TEXDOCS:.tex=.toc)

distclean: clean
	@echo Full clean...
	$(RM) $(TEXDOCS:.tex=.dvi) $(TEXDOCS:.tex=.toc)