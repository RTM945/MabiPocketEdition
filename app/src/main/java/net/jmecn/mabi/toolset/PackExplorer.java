package net.jmecn.mabi.toolset;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import net.jmecn.mabi.pack.PackageEntry;

/**
 * Pack文件浏览器
 * 
 * @author yanmaoyuan
 *
 */
public class PackExplorer extends JFrame {
    private static final long serialVersionUID = -5123336524832172109L;

    private PackageEntry currentEntry = null;

    public PackExplorer() {
        this.setTitle("Pack Explorer");
        this.setSize(1024, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label = new JLabel("null");
        JButton button = new JButton("解压缩");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (currentEntry == null) {
                    return;
                }
                currentEntry.extract();
            }
        });

        DefaultMutableTreeNode node = new DefaultMutableTreeNode("data");
        loadTree(node);

        JTree tree = new JTree(node);
        tree.expandRow(0);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object component = tree.getLastSelectedPathComponent();
                if (component instanceof DefaultMutableTreeNode) {
                    Object obj = ((DefaultMutableTreeNode) component).getUserObject();
                    if (obj instanceof PackageEntry) {
                        PackageEntry entry = (PackageEntry) obj;
                        currentEntry = entry;
                        label.setText(getText(entry));
                    }
                }
            }

        });

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(tree);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(label);
        panel.add(button);

        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        this.getContentPane().add(panel, BorderLayout.SOUTH);
    }

    public void loadTree(DefaultMutableTreeNode node) {
        PackManager manager = PackManager.getInstance();

        List<String> results = manager.listAll("", "");
        Collections.sort(results);
        String[] files = results.toArray(new String[results.size()]);

        try {
            DefaultMutableTreeNode current = null;
            for (int i = 0; i < files.length; i++) {
                String file = files[i];
                String[] names = file.split("/");

                current = node;
                for (int j = 0; j < names.length; j++) {
                    String name = names[j];

                    boolean hasSameNode = false;
                    @SuppressWarnings("rawtypes")
                    Enumeration enumer = current.children();
                    while (enumer.hasMoreElements()) {
                        Object obj = enumer.nextElement();
                        if (obj instanceof DefaultMutableTreeNode) {
                            DefaultMutableTreeNode o = (DefaultMutableTreeNode) obj;
                            Object userObj = o.getUserObject();
                            if (userObj instanceof String) {
                                String s = (String) userObj;
                                if (s.equals(name)) {
                                    current = o;
                                    hasSameNode = true;
                                    break;
                                }
                            } else if (userObj instanceof PackageEntry) {
                                PackageEntry entry = (PackageEntry) userObj;
                                if (entry.toString().equals(name)) {
                                    PackageEntry cur = manager.findPackEntry(file);
                                    if (entry.getSeed() < cur.getSeed()) {
                                        // 用新的替换旧的。
                                        o.setUserObject(cur);
                                    }
                                    current = o;
                                    hasSameNode = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (!hasSameNode) {
                        DefaultMutableTreeNode o = null;
                        if (j == names.length - 1) {
                            o = new DefaultMutableTreeNode(manager.findPackEntry(file));
                        } else {
                            o = new DefaultMutableTreeNode(name);
                        }
                        current.add(o);
                        current = o;
                    }
                }
                current = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getText(PackageEntry entry) {
        String format = "种子：%d, Offset：%08X, 文件大小：%s, 解压后：%s, %s";
        return String.format(format, entry.getSeed(), entry.getOffset(), getFileSize(entry.getCompressedSize()),
                getFileSize(entry.getDecompressedSize()), entry.getName());
    }

    private String getFileSize(int size) {
        String[] units = { "B", "KiB", "MiB", "GiB", "TiB" };
        int n = 0;
        int hi = size;
        int low = 0;
        while (hi > 1024 && n < units.length - 1) {
            hi = size / 1024;
            low = size % 1024;
            n++;
        }
        return hi + (low > 0 ? "." + (int) (low / 1024f * 100) : "") + " " + units[n];
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        new PackExplorer().setVisible(true);
    }
}
