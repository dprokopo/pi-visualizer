/* 
 * Copyright 2017 Dagmar Prokopova <xproko26@stud.fit.vutbr.cz>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.vutbr.fit.xproko26.pivis.parser;

import cz.vutbr.fit.xproko26.pivis.antlr.PiExprBaseVisitor;
import cz.vutbr.fit.xproko26.pivis.antlr.PiExprParser;
import static java.util.stream.Collectors.toList;
import cz.vutbr.fit.xproko26.pivis.model.names.NVList;
import cz.vutbr.fit.xproko26.pivis.model.names.NameValue;

/**
 * NVListVisitor processes name list parser context and converts it into
 * name value list (see {@link NVList NVList}).
 * @author Dagmar Prokopova
 */
public class NVListVisitor extends PiExprBaseVisitor<NVList> {
    
    @Override
    public NVList visitNlist(PiExprParser.NlistContext ctx) {        
        return new NVList(ctx.NAME().stream().map(n -> new NameValue(n.getText())).collect(toList()));
    }
}
