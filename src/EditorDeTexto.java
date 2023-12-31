import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class EditorDeTexto extends JFrame implements ActionListener {
    private final JTextArea areaTexto;
    private final JFileChooser fileChooser;
    private String nombreArchivo;
    private JTextField buscarCampo;
    private final Highlighter highlighter;

    private boolean modoOscuro = true;

    public EditorDeTexto() {
        setTitle("Editor de Texto");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(30, 30, 30)); // Color oscuro para el fondo de la ventana

        areaTexto = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(areaTexto);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane);


        JMenuBar menuBar = new JMenuBar();

        JMenu archivoMenu = new JMenu("Archivo");
        JMenuItem nuevoItem = new JMenuItem("Nuevo");
        JMenuItem abrirItem = new JMenuItem("Abrir");
        JMenuItem guardarItem = new JMenuItem("Guardar");
        JMenuItem guardarComoItem = new JMenuItem("Guardar como");
        JMenuItem cerrarItem = new JMenuItem("Cerrar");
        

        archivoMenu.add(nuevoItem);
        archivoMenu.add(abrirItem);
        archivoMenu.add(guardarItem);
        archivoMenu.add(guardarComoItem);
        archivoMenu.add(cerrarItem);
        
        

        JMenu edicionMenu = new JMenu("Edición");
        JMenuItem buscarItem = new JMenuItem("Buscar");
        JMenuItem reemplazarItem = new JMenuItem("Reemplazar");
        JMenuItem copiarItem = new JMenuItem("Copiar");
        JMenuItem cortarItem = new JMenuItem("Cortar");
        JMenuItem pegarItem = new JMenuItem("Pegar");
        JMenuItem analizarItem = new JMenuItem("Analizar");


        edicionMenu.add(buscarItem);
        edicionMenu.add(reemplazarItem);
        edicionMenu.add(copiarItem);
        edicionMenu.add(cortarItem);
        edicionMenu.add(pegarItem);
        edicionMenu.add(analizarItem);

        JMenuItem cambiarModoItem = new JMenuItem("Cambiar Modo");
        archivoMenu.add(cambiarModoItem);

        menuBar.add(archivoMenu);
        menuBar.add(edicionMenu);

        setJMenuBar(menuBar);

        // Asociar combinaciones de teclas a las funciones
        nuevoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        abrirItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        guardarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        guardarComoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        cerrarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));

        buscarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        reemplazarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK));
        copiarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        cortarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        pegarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        analizarItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));

        nuevoItem.addActionListener(this);
        abrirItem.addActionListener(this);
        guardarItem.addActionListener(this);
        guardarComoItem.addActionListener(this);
        cerrarItem.addActionListener(this);

        buscarItem.addActionListener(this);
        reemplazarItem.addActionListener(this);
        copiarItem.addActionListener(this);
        cortarItem.addActionListener(this);
        pegarItem.addActionListener(this);
        analizarItem.addActionListener(this);
        cambiarModoItem.addActionListener(this);

        fileChooser = new JFileChooser();

        if (modoOscuro) {
            configurarTemaOscuro();
        }

        highlighter = areaTexto.getHighlighter();

        setVisible(true);
    }

    private void configurarTemaOscuro() {
        areaTexto.setBackground(new Color(30, 30, 30));
        areaTexto.setForeground(new Color(200, 200, 200));
        getContentPane().setBackground(new Color(30, 30, 30)); // Color oscuro para el fondo de la ventana
    }

    private void configurarTemaClaro() {
        areaTexto.setBackground(Color.WHITE);
        areaTexto.setForeground(Color.BLACK);
        getContentPane().setBackground(Color.WHITE); // Color claro para el fondo de la ventana
    }

    private void alternarModo() {
        modoOscuro = !modoOscuro;
        if (modoOscuro) {
            configurarTemaOscuro();
        } else {
            configurarTemaClaro();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        switch (comando) {
            case "Nuevo":
                areaTexto.setText("");
                nombreArchivo = null;
                break;

            case "Abrir":
                int opcion = fileChooser.showOpenDialog(this);
                if (opcion == JFileChooser.APPROVE_OPTION) {
                    File archivo = fileChooser.getSelectedFile();
                    nombreArchivo = archivo.getAbsolutePath();
                    try {
                        BufferedReader lector = new BufferedReader(new FileReader(nombreArchivo));
                        areaTexto.setText("");
                        String linea;
                        while ((linea = lector.readLine()) != null) {
                            areaTexto.append(linea + "\n");
                        }
                        lector.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                break;

            case "Guardar":
                if (nombreArchivo != null) {
                    try {
                        BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivo));
                        escritor.write(areaTexto.getText());
                        escritor.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    guardarComo();
                }
                break;

            case "Guardar como":
                guardarComo();
                break;

            case "Cerrar":
                System.exit(0);
                break;

            case "Buscar":
                buscarTexto();
                break;

            case "Reemplazar":
                reemplazarTexto();
                break;

            case "Copiar":
                areaTexto.copy();
                break;

            case "Cortar":
                areaTexto.cut();
                break;

            case "Pegar":
                areaTexto.paste();
                break;

            case "Analizar":
                analizarTexto();
                break;

            case "Cambiar Modo":
                alternarModo();
                break;
        }
    }

    private void buscarTexto() {
        buscarCampo = new JTextField();
        Object[] message = {"Texto a buscar:", buscarCampo};
        int option = JOptionPane.showConfirmDialog(this, message, "Buscar", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String textoBuscar = buscarCampo.getText();
            buscarEnTexto(textoBuscar);
        }
    }

    private void buscarEnTexto(String textoBuscar) {
        if (textoBuscar != null && !textoBuscar.isEmpty()) {
            String texto = areaTexto.getText();
            int inicio = texto.indexOf(textoBuscar);
            if (inicio != -1) {
                int fin = inicio + textoBuscar.length();
                resaltarTexto(inicio, fin);
            } else {
                JOptionPane.showMessageDialog(this, "Texto no encontrado.", "Resultado de la búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void resaltarTexto(int inicio, int fin) {
        // Limpiar resaltados anteriores
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for (Highlighter.Highlight highlight : highlights) {
            highlighter.removeHighlight(highlight);
        }

        try {
            highlighter.addHighlight(inicio, fin, DefaultHighlighter.DefaultPainter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void reemplazarTexto() {
        buscarCampo = new JTextField();
        JTextField reemplazarCampo = new JTextField();
        Object[] message = {"Texto a buscar:", buscarCampo, "Reemplazar con:",reemplazarCampo };
        int option = JOptionPane.showConfirmDialog(this, message, "Reemplazar", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String textoBuscar = buscarCampo.getText();
            String textoReemplazar = reemplazarCampo.getText();
            reemplazarEnTexto(textoBuscar, textoReemplazar);
        }
    }

    private void reemplazarEnTexto(String textoBuscar, String textoReemplazar) {
        if (textoBuscar != null && !textoBuscar.isEmpty()) {
            String texto = areaTexto.getText();
            int inicio = texto.indexOf(textoBuscar);
            if (inicio != -1) {
                int fin = inicio + textoBuscar.length();
                areaTexto.replaceRange(textoReemplazar, inicio, fin);
            } else {
                JOptionPane.showMessageDialog(this, "Texto no encontrado.", "Resultado del reemplazo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void guardarComo() {
        int opcion = fileChooser.showSaveDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            nombreArchivo = archivo.getAbsolutePath();
            try {
                BufferedWriter escritor = new BufferedWriter(new FileWriter(nombreArchivo));
                escritor.write(areaTexto.getText());
                escritor.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void analizarTexto() {
        System.out.println("Analizando texto"); 
        String codigo = areaTexto.getText();
        AnalizadorLexico analizador = new AnalizadorLexico();
        ArrayList<Token> tokens = analizador.analizarCodigo(codigo);
    
        if (!tokens.isEmpty()) {
            StringBuilder resultados = new StringBuilder();
            
            for (Token token : tokens) {
            System.out.println("Tipo: " + token.getTipo() + ", Valor: " + token.getValor()); // Mensaje de depuración
            resultados.append("Tipo: ").append(token.getTipo()).append(", Valor: ").append(token.getValor()).append("\n");
        }
        JOptionPane.showMessageDialog(this, resultados.toString(), "Resultados del análisis léxico", JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("La lista de tokens está vacía."); // Mensaje de depuración
        }
        
        
    }
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EditorDeTexto::new);
    }
}
