;; Produced by JasminVisitor (BCEL)
;; http://bcel.sourceforge.net/
;; Sun Feb 22 22:26:42 GMT 2004

.source <Unknown>
.class public a
.super java/awt/Frame

.field public a [I

.method public final processEvent(Ljava/awt/AWTEvent;)V
.limit stack 4
.limit locals 3
.var 0 is this La; from Label1 to Label2
.var 1 is arg0 Ljava/awt/AWTEvent; from Label1 to Label2

Label1:
	aload_0
	getfield a.a [I
	astore_0
	aload_1
	instanceof java/awt/event/KeyEvent
	ifeq Label0
	aload_0
	aload_1
	checkcast java/awt/event/KeyEvent
	invokevirtual java/awt/event/KeyEvent/getKeyCode()I
	aload_1
	invokevirtual java/awt/AWTEvent/getID()I
	iconst_1
	iand
	iastore
	return
Label0:
	aload_1
	checkcast java/awt/event/MouseEvent
	astore_1
	aload_0
	iconst_0
	aload_1
	invokevirtual java/awt/event/InputEvent/getModifiersEx()I
	iastore
	aload_0
	iconst_1
	aload_1
	invokevirtual java/awt/event/MouseEvent/getX()I
	iastore
	aload_0
	iconst_2
	aload_1
	invokevirtual java/awt/event/MouseEvent/getY()I
	iastore
Label2:
	return

.end method

.method public <init>()V
.limit stack 12
.limit locals 35
.var 0 is this La; from Label65 to Label66

Label65:
	aload_0
	dup
	astore 8
	invokespecial java/awt/Frame/<init>()V
	aload 8
	sipush 256
	newarray int
	putfield a.a [I
Label67:
	aload 8
	getfield a.a [I
	astore 10
	invokestatic javax/sound/midi/MidiSystem/getSynthesizer()Ljavax/sound/midi/Synthesizer;
	dup
	invokeinterface javax/sound/midi/MidiDevice/open()V 1
	invokeinterface javax/sound/midi/Synthesizer/getChannels()[Ljavax/sound/midi/MidiChannel; 1
	dup
	astore 4
	iconst_0
	aaload
	bipush 118
	invokeinterface javax/sound/midi/MidiChannel/programChange(I)V 2
	aload 4
	iconst_0
	aaload
	bipush 91
	iconst_0
	invokeinterface javax/sound/midi/MidiChannel/controlChange(II)V 3
	aload 4
	iconst_1
	aaload
	bipush 127
	invokeinterface javax/sound/midi/MidiChannel/programChange(I)V 2
	aload 4
	iconst_1
	aaload
	bipush 7
	bipush 127
	invokeinterface javax/sound/midi/MidiChannel/controlChange(II)V 3
	aload 4
	iconst_2
	aaload
	bipush 98
	invokeinterface javax/sound/midi/MidiChannel/programChange(I)V 2
	aload 4
	iconst_3
	aaload
	bipush 116
	invokeinterface javax/sound/midi/MidiChannel/programChange(I)V 2
	aload 4
	iconst_3
	aaload
	bipush 7
	bipush 127
	invokeinterface javax/sound/midi/MidiChannel/controlChange(II)V 3
	aload 8
	iconst_1
	invokevirtual java/awt/Frame/setUndecorated(Z)V
	aload 8
	invokevirtual java/awt/Window/getGraphicsConfiguration()Ljava/awt/GraphicsConfiguration;
	astore_0
	new java/awt/Color
	dup
	fconst_1
	fconst_1
	fconst_1
	fconst_1
	invokespecial java/awt/Color/<init>(FFFF)V
	astore 6
	new java/awt/Color
	dup
	fconst_0
	fconst_0
	fconst_1
	fconst_1
	invokespecial java/awt/Color/<init>(FFFF)V
	astore 7
	aload_0
	invokevirtual java/awt/GraphicsConfiguration/getDevice()Ljava/awt/GraphicsDevice;
	dup
	astore_1
	aload 8
	invokevirtual java/awt/GraphicsDevice/setFullScreenWindow(Ljava/awt/Window;)V
	aload_1
	new java/awt/DisplayMode
	dup
	sipush 1024
	sipush 768
	bipush 16
	bipush 60
	invokespecial java/awt/DisplayMode/<init>(IIII)V
	invokevirtual java/awt/GraphicsDevice/setDisplayMode(Ljava/awt/DisplayMode;)V
	sipush 10100
	bipush 16
	multianewarray [[F 2
	astore 5
	iconst_0
	istore 9
	fconst_0
	fstore 11
	fconst_0
	fstore 12
	iconst_4
	bipush 90
	multianewarray [[Ljava/awt/image/BufferedImage; 2
	astore 15
	aload 8
	invokevirtual java/lang/Object/getClass()Ljava/lang/Class;
	ldc "0"
	invokevirtual java/lang/Class/getResource(Ljava/lang/String;)Ljava/net/URL;
	invokestatic javax/imageio/ImageIO/read(Ljava/net/URL;)Ljava/awt/image/BufferedImage;
	astore_1
	aload 15
	iconst_0
	aaload
	iconst_0
	aload_0
	bipush 32
	bipush 32
	iconst_3
	invokevirtual java/awt/GraphicsConfiguration/createCompatibleImage(III)Ljava/awt/image/BufferedImage;
	dup_x2
	aastore
	invokevirtual java/awt/image/BufferedImage/createGraphics()Ljava/awt/Graphics2D;
	aload_1
	iconst_5
	iconst_3
	aconst_null
	invokevirtual java/awt/Graphics/drawImage(Ljava/awt/Image;IILjava/awt/image/ImageObserver;)Z
	pop
	aload 15
	iconst_1
	aaload
	iconst_0
	aload_0
	bipush 32
	bipush 32
	iconst_3
	invokevirtual java/awt/GraphicsConfiguration/createCompatibleImage(III)Ljava/awt/image/BufferedImage;
	dup_x2
	aastore
	invokevirtual java/awt/image/BufferedImage/createGraphics()Ljava/awt/Graphics2D;
	aload_1
	bipush 6
	bipush -25
	aconst_null
	invokevirtual java/awt/Graphics/drawImage(Ljava/awt/Image;IILjava/awt/image/ImageObserver;)Z
	pop
	aload 15
	iconst_2
	aaload
	iconst_0
	aload_0
	bipush 32
	bipush 32
	iconst_3
	invokevirtual java/awt/GraphicsConfiguration/createCompatibleImage(III)Ljava/awt/image/BufferedImage;
	dup_x2
	aastore
	invokevirtual java/awt/image/BufferedImage/createGraphics()Ljava/awt/Graphics2D;
	dup
	astore_1
	aload 7
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	aload_1
	bipush 15
	iconst_5
	iconst_2
	bipush 22
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	aload 15
	iconst_3
	aaload
	iconst_0
	aload_0
	bipush 32
	bipush 32
	iconst_3
	invokevirtual java/awt/GraphicsConfiguration/createCompatibleImage(III)Ljava/awt/image/BufferedImage;
	dup_x2
	aastore
	invokevirtual java/awt/image/BufferedImage/createGraphics()Ljava/awt/Graphics2D;
	dup
	astore_1
	new java/awt/Color
	dup
	fconst_1
	fconst_1
	fconst_0
	fconst_1
	invokespecial java/awt/Color/<init>(FFFF)V
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	aload_1
	bipush 15
	iconst_5
	iconst_2
	bipush 22
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	aload_0
	sipush 1024
	sipush 768
	iconst_3
	invokevirtual java/awt/GraphicsConfiguration/createCompatibleImage(III)Ljava/awt/image/BufferedImage;
	dup
	astore 16
	invokevirtual java/awt/image/BufferedImage/createGraphics()Ljava/awt/Graphics2D;
	dup
	astore_1
	new java/awt/Color
	dup
	fconst_0
	fconst_0
	fconst_0
	ldc 0.13
	invokespecial java/awt/Color/<init>(FFFF)V
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	aload_1
	iconst_0
	iconst_0
	sipush 1024
	sipush 768
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	iconst_0
	istore_2
Label3:
	iload_2
	iconst_4
	if_icmpge Label0
	aload 15
	iload_2
	aaload
	iconst_0
	aaload
	astore 13
	iconst_0
	istore_3
Label2:
	iload_3
	bipush 90
	if_icmpge Label1
	aload 15
	iload_2
	aaload
	iload_3
	aload_0
	bipush 32
	bipush 32
	iconst_3
	invokevirtual java/awt/GraphicsConfiguration/createCompatibleImage(III)Ljava/awt/image/BufferedImage;
	dup_x2
	aastore
	invokevirtual java/awt/image/BufferedImage/createGraphics()Ljava/awt/Graphics2D;
	dup
	astore_1
	iload_3
	i2f
	ldc 0.06981317
	fmul
	f2d
	bipush 16
	i2d
	dup2
	invokevirtual java/awt/Graphics2D/rotate(DDD)V
	aload_1
	aload 13
	iconst_0
	iconst_0
	aconst_null
	invokevirtual java/awt/Graphics/drawImage(Ljava/awt/Image;IILjava/awt/image/ImageObserver;)Z
	pop
	iinc 3 1
	goto Label2
Label1:
	iinc 2 1
	goto Label3
Label0:
	iconst_0
	istore_0
Label5:
	iload_0
	bipush 100
	if_icmpge Label4
	aload 5
	iload_0
	sipush 10000
	iadd
	aaload
	dup
	astore_1
	iconst_0
	sipush 1024
	i2f
	invokestatic java/lang/Math/random()D
	d2f
	fmul
	fastore
	aload_1
	iconst_1
	sipush 768
	i2f
	invokestatic java/lang/Math/random()D
	d2f
	fmul
	fastore
	aload_1
	iconst_2
	ldc 0.15
	ldc 0.0070000007
	iload_0
	i2f
	fmul
	fadd
	fastore
	iinc 0 1
	goto Label5
Label4:
	aload 8
	new java/awt/Cursor
	dup
	iconst_1
	invokespecial java/awt/Cursor/<init>(I)V
	invokevirtual java/awt/Window/setCursor(Ljava/awt/Cursor;)V
	aload 8
	iconst_2
	invokevirtual java/awt/Window/createBufferStrategy(I)V
	aload 8
	sipush 1024
	sipush 768
	invokevirtual java/awt/Component/createVolatileImage(II)Ljava/awt/image/VolatileImage;
	astore 18
	aload 8
	bipush 56
	i2l
	invokevirtual java/awt/Component/enableEvents(J)V
	iconst_0
	istore 19
	iconst_0
	istore 21
	iconst_0
	istore 22
Label64:
	aload 10
	bipush 27
	iaload
	ifne Label6
	iconst_0
	istore 20
	aload 8
	invokevirtual java/awt/Window/getBufferStrategy()Ljava/awt/image/BufferStrategy;
	dup
	astore 30
	invokevirtual java/awt/image/BufferStrategy/getDrawGraphics()Ljava/awt/Graphics;
	checkcast java/awt/Graphics2D
	dup
	astore_1
	aload 16
	iconst_0
	iconst_0
	aconst_null
	invokevirtual java/awt/Graphics/drawImage(Ljava/awt/Image;IILjava/awt/image/ImageObserver;)Z
	pop
	aload_1
	aload 6
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	iload 19
	ifne Label7
	aload_1
	ldc "Press Space"
	sipush 478
	sipush 379
	invokevirtual java/awt/Graphics2D/drawString(Ljava/lang/String;II)V
	aload 10
	bipush 32
	iaload
	dup
	istore 19
	ifeq Label8
	iconst_0
	istore 22
Label8:
	iconst_1
	istore 9
	iconst_0
	istore 21
	aload 5
	iconst_0
	aaload
	dup
	astore_0
	iconst_0
	sipush 2560
	i2f
	fastore
	aload_0
	iconst_1
	sipush 1920
	i2f
	fastore
	aload_0
	bipush 8
	bipush 14
	i2f
	fastore
	aload_0
	iconst_4
	fconst_0
	fastore
	aload_0
	bipush 9
	sipush 500
	i2f
	fastore
	aload_0
	bipush 13
	bipush 9
	i2f
	fastore
	aload_0
	bipush 11
	bipush 6
	i2f
	fastore
	aload_0
	bipush 14
	sipush 400
	i2f
	fastore
	goto Label9
Label7:
	iconst_0
	istore 17
Label42:
	iload 17
	iload 9
	if_icmpge Label10
	aload 5
	iconst_0
	aaload
	astore_3
	aload 5
	iload 17
	aaload
	dup
	astore_1
	iconst_0
	faload
	fstore_0
	aload_1
	iconst_1
	faload
	fstore_2
	aload_1
	iconst_2
	faload
	fstore 13
	aload_1
	iconst_3
	faload
	fstore 14
	aload_1
	iconst_4
	faload
	f2i
	istore 23
	aload_1
	bipush 8
	faload
	fstore 27
	aload_1
	iconst_5
	faload
	fstore 24
	aload_1
	bipush 6
	faload
	fstore 25
	aload_1
	bipush 7
	faload
	fstore 26
	fload_0
	fload 13
	fadd
	fstore_0
	fload_2
	fload 14
	fadd
	fstore_2
	iload 23
	iconst_1
	if_icmpgt Label11
	ldc 3.1415927
	fload_0
	fload 25
	fsub
	f2d
	fload_2
	fload 26
	fsub
	f2d
	invokestatic java/lang/Math/atan2(DD)D
	d2f
	fsub
	fstore 28
	fload 24
	fload 28
	fload 24
	fsub
	ldc_w 9.424778
	fadd
	ldc 6.2831855
	frem
	ldc 3.1415927
	fsub
	ldc 0.07853982
	invokestatic java/lang/Math/min(FF)F
	ldc -0.07853982
	invokestatic java/lang/Math/max(FF)F
	fadd
	ldc 6.2831855
	fadd
	ldc 6.2831855
	frem
	dup
	fstore 24
	f2d
	invokestatic java/lang/Math/sin(D)D
	d2f
	fstore 28
	fload 24
	f2d
	invokestatic java/lang/Math/cos(D)D
	d2f
	fstore 29
	iload 17
	ifne Label12
	fload_0
	fload 27
	dup
	fstore_3
	fcmpg
	iflt Label13
	fload_0
	sipush 5120
	i2f
	fload 27
	fsub
	dup
	fstore_3
	fcmpl
	ifle Label14
Label13:
	fconst_2
	fload_3
	fmul
	fload_0
	fsub
	fstore_0
	fload 13
	fneg
	fstore 13
Label14:
	fload_2
	fload 27
	dup
	fstore_3
	fcmpg
	iflt Label15
	fload_2
	sipush 3840
	i2f
	fload 27
	fsub
	dup
	fstore_3
	fcmpl
	ifle Label16
Label15:
	fconst_2
	fload_3
	fmul
	fload_2
	fsub
	fstore_2
	fload 14
	fneg
	fstore 14
Label16:
	sipush 512
	i2f
	fload_0
	fsub
	fload 13
	bipush 8
	i2f
	fmul
	fsub
	fload 28
	bipush 64
	i2f
	fmul
	fadd
	fconst_0
	invokestatic java/lang/Math/min(FF)F
	sipush -4096
	i2f
	invokestatic java/lang/Math/max(FF)F
	fstore 11
	sipush 384
	i2f
	fload_2
	fsub
	fload 14
	bipush 8
	i2f
	fmul
	fsub
	fload 29
	bipush 64
	i2f
	fmul
	fsub
	fconst_0
	invokestatic java/lang/Math/min(FF)F
	sipush -3072
	i2f
	invokestatic java/lang/Math/max(FF)F
	fstore 12
	aload_1
	bipush 12
	aload 10
	iconst_0
	iaload
	sipush 1024
	iand
	i2f
	fastore
	aload 10
	iconst_1
	iaload
	i2f
	fload 11
	fsub
	fstore 25
	aload 10
	iconst_2
	iaload
	i2f
	fload 12
	fsub
	fstore 26
	aload 10
	bipush 40
	iaload
	aload 10
	bipush 38
	iaload
	isub
	aload 10
	iconst_0
	iaload
	bipush 12
	ishr
	isub
	i2f
	ldc 0.3
	fmul
	fstore 33
	aload 10
	bipush 39
	iaload
	aload 10
	bipush 37
	iaload
	isub
	i2f
	ldc 0.2
	fmul
	fstore 31
	goto Label17
Label12:
	iinc 20 1
	fload_0
	f2d
	fload_2
	f2d
	fload_0
	f2d
	fload_2
	f2d
	aload_3
	iconst_0
	faload
	f2d
	aload_3
	iconst_1
	faload
	f2d
	invokestatic java/awt/geom/Line2D/ptSegDist(DDDDDD)D
	d2f
	fstore 32
	fconst_0
	fstore 31
	ldc -0.3
	fstore 33
	fload 32
	sipush 600
	i2f
	fcmpg
	ifge Label18
	fload 32
	sipush 150
	i2f
	fcmpg
	ifge Label19
	iload 17
	iconst_2
	irem
	i2f
	ldc 0.5
	fsub
	fstore 31
Label19:
	aload_3
	iconst_0
	faload
	aload_3
	iconst_2
	faload
	fload 32
	fmul
	bipush 50
	i2f
	fdiv
	fadd
	fstore 25
	aload_3
	iconst_1
	faload
	aload_3
	iconst_3
	faload
	fload 32
	fmul
	bipush 50
	i2f
	fdiv
	fadd
	fstore 26
	aload_1
	bipush 12
	sipush 1024
	i2f
	fastore
	goto Label17
Label18:
	fload_0
	f2d
	fload_2
	f2d
	fload_0
	f2d
	fload_2
	f2d
	fload 25
	f2d
	fload 26
	f2d
	invokestatic java/awt/geom/Line2D/ptSegDist(DDDDDD)D
	d2f
	bipush 50
	i2f
	fcmpg
	ifge Label17
	aload_1
	bipush 12
	fconst_0
	fastore
	sipush 5120
	i2f
	invokestatic java/lang/Math/random()D
	d2f
	fmul
	fstore 25
	sipush 3840
	i2f
	invokestatic java/lang/Math/random()D
	d2f
	fmul
	fstore 26
Label17:
	fload 13
	ldc 0.95
	fmul
	fload 28
	fload 33
	fmul
	fadd
	fload 29
	fload 31
	fmul
	fsub
	fstore 13
	fload 14
	ldc 0.95
	fmul
	fload 29
	fload 33
	fmul
	fsub
	fload 28
	fload 31
	fmul
	fsub
	fstore 14
	aload_1
	bipush 15
	dup2
	faload
	fconst_1
	fsub
	fastore
	aload_1
	bipush 10
	dup2
	faload
	fconst_1
	fsub
	dup_x2
	fastore
	fconst_0
	fcmpg
	ifge Label22
	aload_1
	bipush 12
	faload
	sipush 1024
	i2f
	fcmpl
	ifne Label22
	iload 17
	ifne Label24
	aload 4
	iconst_0
	aaload
	bipush 80
	bipush 100
	invokeinterface javax/sound/midi/MidiChannel/noteOn(II)V 3
Label24:
	aload 5
	iload 9
	iinc 9 1
	aaload
	dup
	astore_3
	iconst_0
	fload_0
	fload 29
	aload_1
	bipush 13
	faload
	fmul
	fsub
	fload 13
	fsub
	fastore
	aload_3
	iconst_1
	fload_2
	fload 28
	aload_1
	bipush 13
	faload
	fmul
	fsub
	fload 14
	fsub
	fastore
	aload_3
	iconst_2
	fload 13
	fload 28
	bipush -10
	i2f
	fmul
	fadd
	fastore
	aload_3
	iconst_3
	fload 14
	fload 29
	bipush 10
	i2f
	fmul
	fadd
	fastore
	aload_3
	iconst_5
	fload 24
	fastore
	aload_3
	bipush 8
	fconst_1
	fastore
	aload_3
	iconst_4
	iload 23
	iconst_2
	iadd
	i2f
	fastore
	aload_3
	bipush 14
	bipush 50
	i2f
	fastore
	aload_3
	bipush 9
	bipush 100
	i2f
	fastore
	aload_3
	bipush 15
	fconst_0
	fastore
	aload_1
	bipush 13
	aload_1
	bipush 13
	faload
	fneg
	fastore
	aload_1
	bipush 10
	aload_1
	bipush 11
	faload
	fastore
	goto Label22
Label11:
	aload_1
	bipush 9
	dup2
	faload
	fconst_1
	fsub
	fastore
Label22:
	iconst_0
	istore 28
Label36:
	iload 28
	iload 9
	if_icmpge Label26
	aload 5
	iload 28
	aaload
	astore_3
	iload 23
	ifne Label27
	aload_3
	iconst_4
	faload
	fconst_1
	fcmpl
	ifeq Label28
	aload_3
	iconst_4
	faload
	iconst_3
	i2f
	fcmpl
	ifge Label28
Label27:
	iload 23
	iconst_1
	if_icmpne Label30
	aload_3
	iconst_4
	faload
	fconst_2
	fcmpl
	ifne Label30
Label28:
	fload_0
	f2d
	fload_2
	f2d
	fload_0
	fload 13
	fsub
	f2d
	fload_2
	fload 14
	fsub
	f2d
	aload_3
	iconst_0
	faload
	f2d
	aload_3
	iconst_1
	faload
	f2d
	invokestatic java/awt/geom/Line2D/ptSegDist(DDDDDD)D
	d2f
	fload 27
	aload_3
	bipush 8
	faload
	fadd
	fcmpg
	ifge Label30
	aload_1
	bipush 15
	faload
	bipush 28
	i2f
	fcmpg
	ifgt Label33
	aload_3
	iconst_4
	faload
	iconst_3
	i2f
	fcmpl
	ifle Label34
	aload 4
	iconst_2
	aaload
	bipush 89
	bipush 50
	invokeinterface javax/sound/midi/MidiChannel/noteOn(II)V 3
	goto Label33
Label34:
	aload 4
	iconst_1
	aaload
	bipush 55
	bipush 127
	invokeinterface javax/sound/midi/MidiChannel/noteOn(II)V 3
Label33:
	aload_1
	bipush 9
	dup2
	faload
	aload_3
	bipush 14
	faload
	fsub
	fastore
	aload_3
	bipush 9
	dup2
	faload
	aload_1
	bipush 14
	faload
	fsub
	fastore
	aload_1
	bipush 15
	bipush 30
	i2f
	fastore
Label30:
	iinc 28 1
	goto Label36
Label26:
	aload_1
	iconst_0
	fload_0
	fastore
	aload_1
	iconst_1
	fload_2
	fastore
	aload_1
	iconst_2
	fload 13
	fastore
	aload_1
	iconst_3
	fload 14
	fastore
	aload_1
	iconst_5
	fload 24
	fastore
	aload_1
	bipush 6
	fload 25
	fastore
	aload_1
	bipush 7
	fload 26
	fastore
	aload_1
	bipush 9
	faload
	fconst_0
	fcmpg
	ifgt Label37
	iload 23
	ifne Label38
	iconst_0
	istore 19
Label38:
	iload 23
	iconst_1
	if_icmpne Label39
	aload 4
	iconst_3
	aaload
	bipush 40
	bipush 127
	invokeinterface javax/sound/midi/MidiChannel/noteOn(II)V 3
	iconst_0
	istore 13
Label41:
	iload 13
	bipush 50
	if_icmpge Label40
	aload 5
	iload 9
	iinc 9 1
	aaload
	dup
	astore_3
	iconst_0
	fload_0
	fastore
	aload_3
	iconst_1
	fload_2
	fastore
	aload_3
	iconst_2
	invokestatic java/lang/Math/random()D
	d2f
	bipush 6
	i2f
	fmul
	iconst_3
	i2f
	fsub
	fastore
	aload_3
	iconst_3
	invokestatic java/lang/Math/random()D
	d2f
	bipush 6
	i2f
	fmul
	iconst_3
	i2f
	fsub
	fastore
	aload_3
	iconst_4
	iconst_4
	i2f
	fastore
	aload_3
	bipush 9
	sipush 400
	i2f
	fastore
	aload_3
	bipush 8
	iconst_3
	i2f
	fastore
	aload_3
	bipush 14
	bipush -5
	i2f
	fastore
	iinc 13 1
	goto Label41
Label40:
	iinc 22 1
Label39:
	aload 5
	iload 17
	dup
	iconst_1
	isub
	istore 17
	aload 5
	iinc 9 -1
	iload 9
	aaload
	aastore
	aload 5
	iload 9
	aload_1
	aastore
Label37:
	iinc 17 1
	goto Label42
Label10:
	iinc 21 1
	iload 21
	sipush 600
	idiv
	iload 20
	if_icmplt Label43
	aload 5
	iload 9
	iinc 9 1
	aaload
	dup
	astore_0
	iconst_4
	fconst_1
	fastore
	aload_0
	bipush 8
	bipush 10
	i2f
	fastore
	aload_0
	bipush 9
	bipush 100
	i2f
	fastore
	aload_0
	bipush 11
	bipush 22
	i2f
	fastore
	aload_0
	bipush 14
	sipush 300
	i2f
	fastore
	iload 9
	iconst_4
	irem
	dup
	istore_1
	iconst_2
	irem
	ifne Label44
	aload_0
	iconst_0
	iload_1
	sipush 2560
	imul
	i2f
	fastore
	aload_0
	iconst_1
	sipush 3840
	i2f
	invokestatic java/lang/Math/random()D
	d2f
	fmul
	fastore
	goto Label45
Label44:
	aload_0
	iconst_0
	sipush 5120
	i2f
	invokestatic java/lang/Math/random()D
	d2f
	fmul
	fastore
	aload_0
	iconst_1
	iinc 1 -1
	iload_1
	sipush 1920
	imul
	i2f
	fastore
Label45:
	aload_0
	bipush 6
	aload_0
	iconst_0
	faload
	fastore
	aload_0
	bipush 7
	aload_0
	iconst_1
	faload
	fastore
Label43:
	aload 18
	invokevirtual java/awt/image/VolatileImage/createGraphics()Ljava/awt/Graphics2D;
	dup
	astore_1
	aload 16
	iconst_0
	iconst_0
	aconst_null
	invokevirtual java/awt/Graphics/drawImage(Ljava/awt/Image;IILjava/awt/image/ImageObserver;)Z
	pop
	sipush 10000
	istore_3
Label47:
	iload_3
	sipush 10100
	if_icmpge Label46
	aload 5
	iload_3
	aaload
	dup
	astore_2
	iconst_2
	faload
	fstore_0
	aload_1
	new java/awt/Color
	dup
	fload_0
	dup
	dup
	fconst_1
	invokespecial java/awt/Color/<init>(FFFF)V
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	aload_1
	sipush 5120
	i2f
	fload 11
	fadd
	fload_0
	fmul
	aload_2
	iconst_0
	faload
	fadd
	f2i
	sipush 1024
	irem
	sipush 3840
	i2f
	fload 12
	fadd
	fload_0
	fmul
	aload_2
	iconst_1
	faload
	fadd
	f2i
	sipush 768
	irem
	iconst_2
	iconst_2
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	iinc 3 1
	goto Label47
Label46:
	iconst_0
	istore_3
	iconst_1
	istore_0
Label58:
	iload_3
	iflt Label9
	iload_3
	iload 9
	if_icmplt Label49
	iconst_m1
	istore_0
	aload 30
	invokevirtual java/awt/image/BufferStrategy/getDrawGraphics()Ljava/awt/Graphics;
	checkcast java/awt/Graphics2D
	dup
	astore_1
	aload 18
	iconst_0
	iconst_0
	aconst_null
	invokevirtual java/awt/Graphics/drawImage(Ljava/awt/Image;IILjava/awt/image/ImageObserver;)Z
	pop
	goto Label50
Label49:
	aload 5
	iload_3
	aaload
	astore_2
	fload 11
	aload_2
	iconst_0
	faload
	fadd
	f2i
	istore 14
	fload 12
	aload_2
	iconst_1
	faload
	fadd
	f2i
	istore 17
	aload_2
	iconst_4
	faload
	f2i
	istore 13
	iload_0
	iconst_1
	if_icmpne Label51
	iload 13
	iconst_1
	if_icmpgt Label52
Label51:
	iload_0
	iconst_m1
	if_icmpne Label50
	iload 13
	iconst_1
	if_icmpgt Label50
Label52:
	iload 13
	iconst_4
	if_icmpne Label55
	aload_1
	new java/awt/Color
	dup
	fconst_1
	fconst_0
	fconst_0
	fconst_1
	invokespecial java/awt/Color/<init>(FFFF)V
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	aload_1
	iload 14
	iload 17
	iconst_3
	iconst_3
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	goto Label50
Label55:
	aload_2
	bipush 15
	faload
	fconst_0
	fcmpl
	ifle Label57
	aload_2
	bipush 15
	faload
	bipush 30
	i2f
	fdiv
	fstore 20
	aload_2
	bipush 9
	faload
	sipush 500
	i2f
	invokestatic java/lang/Math/min(FF)F
	sipush 500
	i2f
	fdiv
	fload 20
	fmul
	fstore 23
	aload_1
	new java/awt/Color
	dup
	fload 20
	fload 23
	fsub
	fconst_0
	fload 23
	fconst_1
	invokespecial java/awt/Color/<init>(FFFF)V
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	aload_1
	iload 14
	bipush 29
	isub
	iload 17
	bipush 29
	isub
	bipush 58
	bipush 58
	invokevirtual java/awt/Graphics/fillOval(IIII)V
Label57:
	aload_1
	aload 15
	iload 13
	aaload
	ldc 14.323944
	aload_2
	iconst_5
	faload
	fmul
	f2i
	aaload
	iload 14
	bipush 16
	isub
	iload 17
	bipush 16
	isub
	aconst_null
	invokevirtual java/awt/Graphics/drawImage(Ljava/awt/Image;IILjava/awt/image/ImageObserver;)Z
	pop
Label50:
	iload_3
	iload_0
	iadd
	istore_3
	goto Label58
Label9:
	aload_1
	aload 6
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	aload_1
	iload 22
	invokestatic java/lang/String/valueOf(I)Ljava/lang/String;
	sipush 887
	sipush 652
	invokevirtual java/awt/Graphics2D/drawString(Ljava/lang/String;II)V
	aload_1
	sipush 887
	sipush 662
	bipush 127
	iconst_1
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	aload_1
	sipush 1014
	sipush 662
	iconst_1
	bipush 96
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	aload_1
	sipush 888
	sipush 758
	bipush 127
	iconst_1
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	aload_1
	sipush 887
	sipush 663
	iconst_1
	bipush 96
	invokevirtual java/awt/Graphics/fillRect(IIII)V
	iconst_0
	istore_0
Label63:
	iload_0
	iload 9
	if_icmpge Label59
	aload 5
	iload_0
	aaload
	dup
	astore_2
	iconst_4
	faload
	dup
	fstore_3
	fconst_1
	fcmpg
	ifgt Label60
	fload_3
	fconst_0
	fcmpl
	ifne Label61
	aload_1
	aload 7
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
	goto Label62
Label61:
	aload_1
	new java/awt/Color
	dup
	fconst_0
	fconst_1
	fconst_0
	fconst_1
	invokespecial java/awt/Color/<init>(FFFF)V
	invokevirtual java/awt/Graphics/setColor(Ljava/awt/Color;)V
Label62:
	aload_1
	sipush 887
	aload_2
	iconst_0
	faload
	ldc 0.024804687
	fmul
	f2i
	iadd
	sipush 662
	aload_2
	iconst_1
	faload
	ldc_w 0.025
	fmul
	f2i
	iadd
	iconst_2
	iconst_2
	invokevirtual java/awt/Graphics/fillRect(IIII)V
Label60:
	iinc 0 1
	goto Label63
Label59:
	aload 30
Label68:
	invokevirtual java/awt/image/BufferStrategy/show()V
	goto Label64
Label69:
	pop
Label6:
	iconst_0
	invokestatic java/lang/System/exit(I)V
Label66:
	return

.catch java/lang/Exception from Label67 to Label68 using Label69
.end method

.method public static <clinit>()V
.limit stack 2
.limit locals 1

	ldc_w "sun.java2d.translaccel"
	ldc "true"
	invokestatic java/lang/System/setProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
	pop
	ldc_w "sun.java2d.accthreshold"
	ldc "0"
	invokestatic java/lang/System/setProperty(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
	pop
	new a
	dup
	invokespecial a/<init>()V
	pop
	return

.end method