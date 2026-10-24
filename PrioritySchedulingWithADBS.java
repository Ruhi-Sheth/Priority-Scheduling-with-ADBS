package priorityschedulingwithadbs;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
//Ruhi Sheth SYCM3-49
//Preetha Pol SYCM3-49
// Main window and components used by the simulator.
public class PrioritySchedulingWithADBS extends JFrame {
    JSpinner count = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
    JRadioButton non = new JRadioButton("Non-Pre-emptive", true);
    JRadioButton pre = new JRadioButton("Pre-emptive");
    JRadioButton adbs = new JRadioButton("Adaptive Deadline-Based (ADBS)");
    // Input table stores each process's arrival time, burst time and priority.
    DefaultTableModel input = new DefaultTableModel(new Object[]{"Process","Arrival Time","Burst Time","Priority","Deadline"},0) {
        public boolean isCellEditable(int r,int c){ return c>0; }
    };
    // Output table displays the calculated scheduling values.
    DefaultTableModel output = new DefaultTableModel(new Object[]{"PID","AT","BT","Priority","Deadline","CT","TAT","WT","RT","Deadline Status"},0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    JTable inTable = new JTable(input), outTable = new JTable(output);
    GanttPanel gantt = new GanttPanel();
    JLabel avgWT = new JLabel("-"), avgTAT = new JLabel("-"), avgRT = new JLabel("-");
    JTextPane compare = new JTextPane();

    // Build the GUI and connect buttons to their actions.
    PrioritySchedulingWithADBS() {
        setTitle("Priority Scheduling Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1020,820); 
        setMinimumSize(new Dimension(900,700)); 
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        root.setBackground(new Color(241,245,250)); 
        setContentPane(root);

        JLabel title = new JLabel("PRIORITY SCHEDULING SIMULATOR",SwingConstants.CENTER);
        title.setFont(new Font("SansSerif",Font.BOLD,25)); 
        title.setForeground(new Color(15,42,75));
        title.setOpaque(true); 
        title.setBackground(new Color(210,229,250));
        title.setBorder(BorderFactory.createEmptyBorder(15,5,15,5)); 
        root.add(title,BorderLayout.NORTH);

        JPanel all = new JPanel(); all.setOpaque(false);
        all.setLayout(new BoxLayout(all,BoxLayout.Y_AXIS));
        root.add(new JScrollPane(all),BorderLayout.CENTER);

        // Create the process input area and scheduling controls.
        JPanel inp = panel("PROCESS INPUT");
        JPanel controls = new JPanel(); 
        controls.setLayout(new BoxLayout(controls,BoxLayout.Y_AXIS)); 
        controls.setOpaque(false);
        JPanel countRow = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        countRow.setOpaque(false);
        countRow.add(new JLabel("Number of Processes:")); 
        countRow.add(count);
        JPanel typeRow = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        typeRow.setOpaque(false);
        
        JLabel schedulingLabel = new JLabel("Scheduling Type:");
        schedulingLabel.setFont(new Font("SansSerif",Font.BOLD,16));
        non.setFont(new Font("SansSerif",Font.PLAIN,15)); 
        pre.setFont(new Font("SansSerif",Font.PLAIN,15));
        ButtonGroup bg = new ButtonGroup(); 
        bg.add(non); 
        bg.add(pre); bg.add(adbs);
        typeRow.add(schedulingLabel); 
        typeRow.add(non); 
        typeRow.add(pre); typeRow.add(adbs);
        controls.add(countRow); 
        controls.add(typeRow); 
        inp.add(controls,BorderLayout.NORTH);
        centerTableText(inTable); 
        centerTableText(outTable);
        inTable.setRowHeight(27); 
        inp.add(new JScrollPane(inTable),BorderLayout.CENTER);
        
        JButton run = new JButton("Run Scheduling");
        JButton clear = new JButton("Clear"), compareBtn = new JButton("Compare Both Algorithms");
        style(run,true); style(clear,false); style(compareBtn,false);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER)); 
        btns.setOpaque(false);
        btns.add(run); 
        btns.add(clear); 
        btns.add(compareBtn);
        inp.add(btns,BorderLayout.SOUTH); 
        all.add(inp); 
        all.add(Box.createVerticalStrut(10));

        // Create the output area for the Gantt chart, results and comparison.
        JPanel out = panel("OUTPUT");
        JPanel content = new JPanel(); 
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content,BoxLayout.Y_AXIS));
        
        content.add(label("Gantt Chart:")); 
        gantt.setPreferredSize(new Dimension(900,100));
        gantt.setMaximumSize(new Dimension(Integer.MAX_VALUE,105)); 
        content.add(gantt);
        
        content.add(label("RESULTS:")); 
        outTable.setRowHeight(25); 
        content.add(new JScrollPane(outTable));
        JPanel metrics = new JPanel(new GridLayout(1,3,12,5)); 
        metrics.setOpaque(false);
        metrics.add(metric("Average Waiting Time",avgWT));
        metrics.add(metric("Average Turnaround Time",avgTAT));
        metrics.add(metric("Average Response Time",avgRT)); 
        content.add(metrics);
        
        JLabel comparisonHeading = label("ALGORITHM COMPARISON:");
        comparisonHeading.setFont(new Font("SansSerif",Font.BOLD,16)); 
        content.add(comparisonHeading);
        compare.setEditable(false); 
        compare.setFont(new Font("SansSerif",Font.BOLD,15));
        content.add(new JScrollPane(compare)); 
        out.add(content,BorderLayout.CENTER); all.add(out);

        count.addChangeListener(e->makeRows());
        run.addActionListener(e->runSelected()); 
        clear.addActionListener(e->clearAll());
        compareBtn.addActionListener(e->compareAlgorithms());
        makeRows();
    }

    // Create a titled panel with a consistent appearance.
    JPanel panel(String name) {
        JPanel p = new JPanel(new BorderLayout(8,8)); 
        p.setBackground(Color.WHITE);
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(185,202,224)),name);
        border.setTitleFont(new Font("SansSerif",Font.BOLD,16)); 
        p.setBorder(border);
        p.setAlignmentX(Component.LEFT_ALIGNMENT); 
        return p;
    }
    // Center-align the values inside a table.
    void centerTableText(JTable table) {
        javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i=0;i<table.getColumnCount();i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);
    }
    JLabel label(String s) {
        JLabel l = new JLabel(s); 
        l.setFont(new Font("SansSerif",Font.BOLD,14));
        l.setBorder(BorderFactory.createEmptyBorder(6,2,4,2)); 
        return l;
    }
    JPanel metric(String name,JLabel value) {
        JPanel p = new JPanel(new BorderLayout(4,4)); 
        p.setOpaque(false);
        p.add(new JLabel(name),BorderLayout.NORTH);
        value.setOpaque(true); 
        value.setBackground(new Color(246,249,253));
        value.setBorder(BorderFactory.createLineBorder(new Color(180,200,225)));
        p.add(value,BorderLayout.CENTER); 
        return p;
    }
    void style(JButton b,boolean primary) {
        b.setFocusPainted(false); 
        b.setFont(new Font("SansSerif",Font.BOLD,13));
        b.setBackground(primary?new Color(39,125,230):new Color(232,238,246));
        b.setForeground(primary?Color.WHITE:new Color(20,35,55));
    }
    // Create default rows whenever the process count changes.
    void makeRows() {
        input.setRowCount(0);
        for(int i=0;i<(Integer)count.getValue();i++) 
            input.addRow(new Object[]{"P"+(i+1),i,5,i+1,i+8});
    }
    // Reset the results and restore the default input rows.
    void clearAll() {
        output.setRowCount(0); gantt.setSegments(new ArrayList<>());
        avgWT.setText("-"); avgTAT.setText("-"); avgRT.setText("-"); compare.setText(""); makeRows();
    }

    // Read and validate the values entered in the input table.
    java.util.List<Proc> readInput() {
        if(inTable.isEditing()) inTable.getCellEditor().stopCellEditing();
        java.util.List<Proc> list = new ArrayList<>();
        for(int i=0;i<input.getRowCount();i++) {
            try {
                int at=Integer.parseInt(input.getValueAt(i,1).toString().trim());
                int bt=Integer.parseInt(input.getValueAt(i,2).toString().trim());
                int pr=Integer.parseInt(input.getValueAt(i,3).toString().trim());
                int deadline=Integer.parseInt(input.getValueAt(i,4).toString().trim());
                if(at<0) throw new IllegalArgumentException("Arrival Time cannot be negative.");
                if(bt<=0) throw new IllegalArgumentException("Burst Time must be greater than zero.");
                if(pr<0) throw new IllegalArgumentException("Priority cannot be negative.");
                if(deadline<at) throw new IllegalArgumentException("Deadline cannot be earlier than arrival time.");
                Proc proc = new Proc("P"+(i+1),at,bt,pr,i); proc.deadline=deadline; list.add(proc);
            } catch(NumberFormatException ex) {
                throw new IllegalArgumentException("Enter whole numbers for AT, BT and Priority in row "+(i+1)+".");
            }
        }
        return list;
    }
    // Run the algorithm selected by the user.
    void runSelected() {
        try {
            Result r=schedule(readInput(),pre.isSelected() || adbs.isSelected(), adbs.isSelected()); 
            showResult(r);
            compare.setText((adbs.isSelected()?"Adaptive Deadline-Based (ADBS)":pre.isSelected()?"Pre-emptive":"Non-Pre-emptive")+
                " scheduling selected.\nAverage WT: "+fmt(r.avgWT)+
                "    Average TAT: "+fmt(r.avgTAT)+"    Average RT: "+fmt(r.avgRT)+
                "\n"+(adbs.isSelected()?"ADBS selects the process with the least remaining deadline slack.":"Smaller priority number means higher priority."));
        } catch(IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Invalid Input",JOptionPane.ERROR_MESSAGE);
        }
    }
    // Display process results, averages and Gantt chart segments.
    void showResult(Result r) {
        output.setRowCount(0);
        for(Proc p:r.procs) output.addRow(new Object[]{p.id,p.at,p.bt,p.priority,p.deadline,p.ct,p.tat,p.wt,p.rt,p.ct<=p.deadline?"Met":"Missed"});
        avgWT.setText(fmt(r.avgWT)); avgTAT.setText(fmt(r.avgTAT)); avgRT.setText(fmt(r.avgRT));
        gantt.setSegments(r.segments);
    }
    // Run both algorithms on the same input and compare their averages.
    void compareAlgorithms() {
        try {
            java.util.List<Proc> ps=readInput();
            Result a=schedule(ps,false), b=schedule(ps,true);
            compare.setText("NON-PRE-EMPTIVE: Avg WT = "+fmt(a.avgWT)+", Avg TAT = "+fmt(a.avgTAT)+", Avg RT = "+fmt(a.avgRT)+
                "\nPRE-EMPTIVE: Avg WT = "+fmt(b.avgWT)+", Avg TAT = "+fmt(b.avgTAT)+", Avg RT = "+fmt(b.avgRT)+
                "\nLower average WT: "+better(a.avgWT,b.avgWT)+
                "\nLower average TAT: "+better(a.avgTAT,b.avgTAT)+
                "\nLower average RT: "+better(a.avgRT,b.avgRT)+
                "\nLower averages indicate better performance for that parameter.");
        } catch(IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Invalid Input",JOptionPane.ERROR_MESSAGE);
        }
    }
    // Return the algorithm with the lower average value.
    String better(double a,double b){ 
        return a<b?"Non-Pre-emptive":b<a?"Pre-emptive":"Both equal"; }

    // Schedule processes using pre-emptive or non-pre-emptive priority.
    Result schedule(java.util.List<Proc> source,boolean isPreemptive) { return schedule(source,isPreemptive,false); }

    Result schedule(java.util.List<Proc> source,boolean isPreemptive,boolean isAdbs) {
        java.util.List<Proc> ps=new ArrayList<>();
        for(Proc p:source) { Proc copy=new Proc(p.id,p.at,p.bt,p.priority,p.order); copy.deadline=p.deadline; ps.add(copy); }
        java.util.List<Seg> segs=new ArrayList<>(); int time=0,done=0,n=ps.size();
        while(done<n) {
            // Select the highest-priority process that has already arrived.
            Proc chosen=null;
            for(Proc p:ps) if(!p.done && p.at<=time) {
                if(chosen==null || (isAdbs ? (p.deadline-time-p.remaining < chosen.deadline-time-chosen.remaining || (p.deadline-time-p.remaining == chosen.deadline-time-chosen.remaining && p.remaining < chosen.remaining)) : (p.priority<chosen.priority || (p.priority==chosen.priority && p.at<chosen.at) || (p.priority==chosen.priority && p.at==chosen.at && p.order<chosen.order)))) 
                    chosen=p;
            }
            // If no process has arrived, move time to the next arrival.
            if(chosen==null) {
                int next=Integer.MAX_VALUE;
                for(Proc p:ps) if(!p.done) next=Math.min(next,p.at);
                addSegment(segs,"Idle",time,next); 
                time=next; 
                continue;
            }
            if(chosen.firstStart==-1) chosen.firstStart=time;
            // Non-pre-emptive runs to completion; pre-emptive runs one time unit.
            if(!isPreemptive) {
                int start=time; 
                time+=chosen.remaining; 
                chosen.remaining=0;
                chosen.ct=time; 
                chosen.done=true; 
                done++; 
                addSegment(segs,chosen.id,start,time);
            } else {
                int start=time; 
                chosen.remaining--; 
                time++;
                addSegment(segs,chosen.id,start,time);
                if(chosen.remaining==0) { 
                    chosen.ct=time; 
                    chosen.done=true; 
                    done++; }
            }
        }
        // Calculate waiting, turnaround and response times and their averages.
        double wt=0,tat=0,rt=0;
        for(Proc p:ps){ 
            p.tat=p.ct-p.at; p.wt=p.tat-p.bt; p.rt=p.firstStart-p.at; wt+=p.wt; tat+=p.tat; rt+=p.rt; }
        return new Result(ps,segs,wt/n,tat/n,rt/n);
    }
    // Combine adjacent segments when the same process keeps running.
    void addSegment(java.util.List<Seg> segs,String id,int start,int end) {
        if(end<=start) return;
        if(!segs.isEmpty()) {
            Seg last=segs.get(segs.size()-1);
            if(last.id.equals(id)&&last.end==start){ 
                last.end=end; 
                return; }
        }
        segs.add(new Seg(id,start,end));
    }
    String fmt(double n){ 
        return String.format("%.2f",n); }

    // Stores input data and calculated times for one process.
    static class Proc {
        String id; 
        int at,bt,priority,order,remaining,ct,tat,wt,rt,deadline=Integer.MAX_VALUE,firstStart=-1; 
        boolean done=false;
        Proc(String id,int at,int bt,int priority,int order) {
            this.id=id; this.at=at; this.bt=bt; this.priority=priority; this.order=order; remaining=bt;
        }
    }
    // Represents one process block on the Gantt chart.
    static class Seg {
        String id; 
        int start,end;
        Seg(String id,int start,int end){ 
            this.id=id; this.start=start; this.end=end; }
    }
    // Holds the complete schedule and average performance values.
    static class Result {
        java.util.List<Proc> procs; java.util.List<Seg> segments; double avgWT,avgTAT,avgRT;
        Result(java.util.List<Proc> p,java.util.List<Seg> s,double w,double t,double r) {
            procs=p; segments=s; avgWT=w; avgTAT=t; avgRT=r;
        }
    }
    // Draws the schedule timeline in the GUI.
    static class GanttPanel extends JPanel {
        java.util.List<Seg> segments=new ArrayList<>();
        GanttPanel() { setBackground(Color.WHITE); 
        setBorder(BorderFactory.createLineBorder(new Color(190,205,225))); }
        
        void setSegments(java.util.List<Seg> s) {
            segments=new ArrayList<>(); 
            for(Seg x:s) 
                segments.add(new Seg(x.id,x.start,x.end)); 
            repaint();
        }
        // Draw each process block and its start/end times.
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); 
            Graphics2D g2=(Graphics2D)g.create();
            if(segments.isEmpty()){ 
                g2.setColor(Color.GRAY); 
                g2.drawString("Run scheduling to display the Gantt chart.",15,35); 
                g2.dispose(); return; }
            int left=10,right=getWidth()-10,width=right-left,top=10,h=38;
            int start=segments.get(0).start,end=segments.get(segments.size()-1).end,total=Math.max(1,end-start);
            g2.setFont(new Font("SansSerif",Font.BOLD,12));
            for(Seg s:segments) {
                int x1=left+(int)Math.round((s.start-start)*width/(double)total);
                int x2=left+(int)Math.round((s.end-start)*width/(double)total),w=Math.max(1,x2-x1);
                g2.setColor(color(s.id)); g2.fillRect(x1,top,w,h);
                g2.setColor(new Color(115,135,160)); g2.drawRect(x1,top,w,h);
                g2.setColor(new Color(20,35,55));
                FontMetrics fm=g2.getFontMetrics(); 
                int tw=fm.stringWidth(s.id);
                if(w>tw+5) 
                    g2.drawString(s.id,x1+(w-tw)/2,top+23);
                g2.drawString(String.valueOf(s.start),x1,top+h+18);
            }
            g2.drawString(String.valueOf(end),right-5,top+h+18); g2.dispose();
        }
        // Use a different background color for each process.
        Color color(String id) {
            if(id.equals("Idle")) 
                return new Color(225,225,225);
            int n=1; 
            try{ 
                n=Integer.parseInt(id.substring(1)); } 
            catch(Exception ignored){
            }
            Color[] c={new Color(190,220,250),new Color(248,190,215),new Color(190,235,195),
                new Color(255,225,165),new Color(215,200,245),new Color(185,235,230)};
            return c[(n-1)%c.length];
        }
    }
    // Start the application on Swing's event-dispatch thread.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->new PrioritySchedulingWithADBS().setVisible(true));
    }
}
