package net.jmecn.mabi.skill.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.jmecn.mabi.skill.*;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private SkillEngine engine;// 引擎
	private JTree tree = null;
	// private JFileChooser chooser = null;// 文件选择器

	// 控件
	private JPanel contentPane = new JPanel();
	private JTextArea textArea = null;

	public MainFrame() {
		engine = new SkillEngine();
		this.setTitle("Skillinfo");
		this.setSize(800, 600);
		this.setLocation(100, 80);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setJMenuBar(getJMenuBar());
		this.setContentPane(contentPane);
		initLayout();
	}

	/**
	 * 初始化界面布局
	 */
	private void initLayout() {
		contentPane.setLayout(new BorderLayout());
		JSplitPane p = new JSplitPane();
		p.setLeftComponent(new JScrollPane(getTree()));

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		p.setRightComponent(new JScrollPane(textArea));
		p.setDividerLocation(180);
		contentPane.add(p, BorderLayout.CENTER);
	}

	// 刷新树
	public JTree getTree() {
		if (tree == null) {
			tree = new JTree();
			tree.setBackground(Color.WHITE);
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("skill");
			for (int i = 1; i < 10; i++) {
				DefaultMutableTreeNode g = new DefaultMutableTreeNode(
						Skillinfo.category[i]);
				root.add(g);
				List<Skillinfo> list = engine.getSkill(i);
				for (Skillinfo info : list) {
					DefaultMutableTreeNode c = new DefaultMutableTreeNode(info);
					g.add(c);
				}
			}
			tree.setModel(new DefaultTreeModel(root));
			tree.setRootVisible(false);
			tree.addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JTree tree = (JTree) e.getSource();
					TreePath selPath = tree.getSelectionPath();
					if (selPath != null) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath
								.getLastPathComponent();
						if (node.getUserObject() instanceof Skillinfo) {
							Skillinfo info = (Skillinfo) node.getUserObject();
							click(info);
						}
					}
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			});
		}
		return tree;
	}

	private void click(Skillinfo info) {
		StringBuffer buffer = new StringBuffer();
		String descName = info.getDescName();
		List<SkillLevelDetail> details = engine.getSkillLevelDetail(descName);
		System.out.println(descName + " " + details.size());
		buffer.append("======= " + info.getSkillLocalName() + " "
				+ info.getSkillEngName() + " =======\n");
		buffer.append(info.getSkillDesc() + "\n\n");
		buffer.append("======= 如何获得 =======\n");
		buffer.append(info.getHowToGetDesc() + "\n\n");

		int sum = 0;
		for (SkillLevelDetail detail : details) {
			buffer.append("===== Rank " + detail.getSkillLevel() + " =====\n");
			buffer.append(detail.getLevelDescription() + "\n");

			int ap = detail.getAbilityNecessary();
			sum += ap;
			buffer.append("- 必要AP:" + ap + " (累计:" + sum + ")\n");
			buffer.append("- 战斗力: " + detail.getCombatPower() + "\n");
			buffer.append("- 效果:\n" + detail.getEffectDescription() + "\n");
			buffer.append("\n");

			buffer.append("获得经验\t回数\t经验\t修炼方法\n");
			for (String[] args : detail.getConditions()) {
				double ex = Double.parseDouble(args[0]);
				int n = Integer.parseInt(args[1].trim());
				String condition = args[2].trim();
				double exp = ex * n;
				String str = ex
						+ String.format("\t%d\t%.2f\t%s\n", n, exp, condition);
				buffer.append(str);
			}
			buffer.append("\n");
		}
		textArea.setText("");
		textArea.setText(buffer.toString());
		
	}

	public JMenuBar getJMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu file = new JMenu("文件");
		JMenuItem export = new JMenuItem("导出");
		export.setEnabled(false);

		menuBar.add(file);
		file.add(export);
		return menuBar;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame().setVisible(true);
			}
		});
	}

}
