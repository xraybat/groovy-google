// 'Figure 2.3  The “Hello, World” Swing user interface, holding
// the image returned by Google Chart' (Making Java Groovy, p.26)
// with fix: 'new SwingBuilder().edt {' not just 'SwingBuilder.edt {'
//
// url: https://chart.googleapis.com/chart?cht=p3&chs=250x100&chd=t:60,40&chl=Hello|World

import java.awt.BorderLayout as BL
import javax.swing.WindowConstants as WC
import groovy.swing.SwingBuilder
import javax.swing.ImageIcon

def base = 'https://chart.googleapis.com/chart?'
def params = [cht: 'p3', chs: '250x100', chd: 't:60,40', chl: 'hello|world']

String qs = params.collect { k, v -> "$k=$v" }.join('&')  // double quotes for g-string
params.each { k, v -> assert qs.contains("$k=$v") }

new SwingBuilder().edt {  // fix reqd: 'new SwingBuilder().edt {' not 'SwingBuilder.edt {'
  frame(title:'hello, world!',
        pack: true,
        visible: true,
        defaultCloseOperation:WC.EXIT_ON_CLOSE) {
          label(icon:new ImageIcon("$base$qs".toURL()),
                constraints:BL.CENTER)
        }
}
